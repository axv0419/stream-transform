package com.dexcom.streamtransform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Properties;

import org.slf4j.Logger;

@Component
public class StreamTransformer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StreamTransformer.class);

    @Autowired
    private ConfigurationBean streamAppConfig;

    public void run(final String[] args) {

        System.out.println(streamAppConfig.getStreamSettings());


//        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
//
//        // Configure the Streams application.
        final Properties streamsConfiguration = new Properties();
        for (String key: streamAppConfig.getStreamSettings().keySet()){
            streamsConfiguration.put(key,streamAppConfig.getStreamSettings().get(key));
        }
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG , LogAndContinueExceptionHandler.class);
      // Define the processing topology of the Streams application.
        final StreamsBuilder builder = new StreamsBuilder();
        passThroughTransformer(builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
        //
//        // For Dev
        streams.cleanUp();
//
//        // Now run the processing topology via `start()` to begin processing its input data.
        streams.start();
//
//        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }


    /**
     * Define the processing topology for Word Count.
     *
     * @param builder StreamsBuilder to use
     */
    void passThroughTransformer(final StreamsBuilder builder) {

        final KStream<byte[], byte[]> stream = builder.stream(streamAppConfig.getInputTopic());

        final ObjectMapper objectMapper = new ObjectMapper();

        // Parse input message to JSON
        KStream<byte[], StreamRecord> validated = stream.mapValues(( byte[] value) -> {
            String valueString = new String(value);
            String jsonString = null;
            try{
                JsonNode jnode = objectMapper.reader().readTree(valueString);
                jsonString = jnode.toString();
                logger.info("Received Message: "+jsonString);
            }catch (Exception e){
                logger.info("Bad Message: "+valueString);
                return  new StreamRecord(value,null,false);
            }
            return  new StreamRecord(value,jsonString,true);
        });



        // Create 2 branches , Good data and bad data.
        KStream<byte[], StreamRecord>[] twoBranches = validated.branch(
                (key, value) -> value.isValid(), /* first predicate  */
                (key, value) -> !value.isValid() /* second predicate */
        );

        KStream<byte[], StreamRecord> validJsonStream = twoBranches[0];
        KStream<byte[], StreamRecord> inValidDataStream = twoBranches[1];

        // Put good data on output topic
        validJsonStream.mapValues(value -> value.getConvertedString()).to(streamAppConfig.getOutputTopic(),
                Produced.with(Serdes.ByteArray(), Serdes.String()));

//        validJsonStream.groupBy((key, record) -> record.getConvertedString())
//        // Filter Duplicate
//        final KTable<byte[],String> referenceTable = builder.table("test-01.table.in");
//        validJsonStream.join(referenceTable)


        // Put bad data in error topic
        inValidDataStream.mapValues(value -> value.getOriginalContent()).to(streamAppConfig.getErrorTopic(),
                Produced.with(Serdes.ByteArray(), Serdes.ByteArray()));


    }

}
