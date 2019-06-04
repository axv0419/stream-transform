package com.dexcom.streamtransform;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

@Component
public class StreamTransformer implements CommandLineRunner {

    @Autowired
    private StreamAppConfig streamAppConfig;

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
        // Construct a `KStream` from the input topic "streams-plaintext-input", where message values
        // represent lines of text (for the sake of this example, we ignore whatever may be stored
        // in the message keys).  The default key and value serdes will be used.
        final KStream<byte[], byte[]> messageStream = builder.stream(streamAppConfig.getInputTopic());

//        final Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);


        messageStream.map(
            new KeyValueMapper<byte[], byte[], KeyValue<String,String>>() {
                @Override
                public KeyValue<String,String> apply(final byte[] key, final byte[] value) {
                    if (value == null) {
                        return new KeyValue<>(null, null);
                    }
                    String k = new String(key);
                    String v = new String(value);
                    System.out.println( String.format("Kafka Message - Key: %s Value: %s ",k,v));
                    return new KeyValue<String,String>(k, v + k );
                }
        }).to(streamAppConfig.getOutputTopic(), Produced.with(Serdes.String(), Serdes.String()));

//        textLines
//                .foreach(  (key,value ) ->{
//                    System.out.println( new String(key) + ": " + new String(value));
//                })
//
//        ;

//        final KTable<String, Long> wordCounts = textLines
//                // Split each text line, by whitespace, into words.  The text lines are the record
//                // values, i.e. we can ignore whatever data is in the record keys and thus invoke
//                // `flatMapValues()` instead of the more generic `flatMap()`.
//                .flatMapValues(value -> Arrays.asList(pattern.split( new String(value).toLowerCase())))
//                // Group the split data by word so that we can subsequently count the occurrences per word.
//                // This step re-keys (re-partitions) the input data, with the new record key being the words.
//                // Note: No need to specify explicit serdes because the resulting key and value types
//                // (String and String) match the application's default serdes.
//                .groupBy((keyIgnored, word) -> word)
//                // Count the occurrences of each word (record key).
//                .count();
//
//        // Write the `KTable<String, Long>` to the output topic.
//        wordCounts.toStream().to(streamAppConfig.getOutputTopic(), Produced.with(Serdes.String(), Serdes.Long()));
    }

}
