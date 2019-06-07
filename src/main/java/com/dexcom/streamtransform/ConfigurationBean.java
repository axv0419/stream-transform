package com.dexcom.streamtransform;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "stream-connector-config")
public class ConfigurationBean {

    @Value("${stream-connector-config.inputTopic}")
    private String inputTopic;

    @Value("${stream-connector-config.outputTopic}")
    private String outputTopic;

    @Value("${stream-connector-config.kafkaName}")
    private String kafkaName;

    @Value("${stream-connector-config.errorTopic}")
    private String errorTopic;

    private HashMap<String, String> streamSettings;

    public String getInputTopic() {
        return inputTopic;
    }

    public String getOutputTopic() {
        return outputTopic;
    }

    public String getErrorTopic() {
        return errorTopic;
    }

    public String getKafkaName() {
        return kafkaName;
    }

    public HashMap<String, String> getStreamSettings() {
        return streamSettings;
    }

    public void setStreamSettings(HashMap<String, String> streamSettings) {
        this.streamSettings = streamSettings;
    }

}