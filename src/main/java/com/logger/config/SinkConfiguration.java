package main.java.com.logger.config;

import java.util.HashMap;
import java.util.Map;

public class SinkConfiguration {
    private final Map<String, String> properties;

    public SinkConfiguration() {
        this.properties = new HashMap<>();
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    public Map<String, String> getAllProperties() {
        return new HashMap<>(properties);
    }
}