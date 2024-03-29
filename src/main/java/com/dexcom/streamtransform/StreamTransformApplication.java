package com.dexcom.streamtransform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Controller
public class StreamTransformApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamTransformApplication.class, args);
    }

    @Value("${stream-connector-config.kafkaCluserName:development-cluster}")
    private String kafkaCluserName;

    @GetMapping("/kafka/info")
    @ResponseBody
    public Map<String, String> kafkaInfo() {
        HashMap<String, String> map = new HashMap<>();
        map.put("kafkaCluserName",kafkaCluserName);
        return map;
    }

    @GetMapping("/")
    public RedirectView one() {
        return  new RedirectView("/static/index.html");
    }

}
