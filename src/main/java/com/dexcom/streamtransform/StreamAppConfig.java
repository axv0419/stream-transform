package com.dexcom.streamtransform;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "stream-connector-config")
public class StreamAppConfig {

    @Value("${stream-connector-config.inputTopic}")
    private String inputTopic;

    @Value("${stream-connector-config.outputTopic}")
    private String outputTopic;

    private HashMap<String, String> streamSettings;

    public String getInputTopic() {
        return inputTopic;
    }

    public String getOutputTopic() {
        return outputTopic;
    }

    public HashMap<String, String> getStreamSettings() {
        return streamSettings;
    }

    public void setStreamSettings(HashMap<String, String> streamSettings) {
        this.streamSettings = streamSettings;
    }

}