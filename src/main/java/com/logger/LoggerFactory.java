package main.java.com.logger;

import main.java.com.logger.core.Logger;
import main.java.com.logger.core.LoggerConfiguration;
import main.java.com.logger.config.ConfigParser;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoggerFactory {

    private static final ConcurrentHashMap<String, Logger> loggerMap = new ConcurrentHashMap<>();

    private LoggerFactory() {
    }

    public static Logger createLogger(String configFilePath, String namespace) throws IOException {
        LoggerConfiguration config = ConfigParser.parseFromFile(configFilePath);
        return getOrCreateLogger(config, namespace);
    }

    public static Logger createLogger(Map<String, String> properties, String namespace) {
        LoggerConfiguration config = ConfigParser.parseFromProperties(properties);
        return getOrCreateLogger(config, namespace);
    }

    public static Logger createLogger(LoggerConfiguration config, String namespace) {
        if (config == null) {
            throw new IllegalArgumentException("LoggerConfiguration must not be null.");
        }
        return getOrCreateLogger(config, namespace);
    }

    private static Logger getOrCreateLogger(LoggerConfiguration config, String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            throw new IllegalArgumentException("Namespace must not be null or empty.");
        }

        String key = namespace + "-" + config.hashCode(); // Ensure LoggerConfiguration implements hashCode well
        return loggerMap.computeIfAbsent(key, k -> new Logger(config, namespace));
    }
}
