package main.java.com.logger.config;

import main.java.com.logger.core.LogLevel;
import main.java.com.logger.core.LoggerConfiguration;
import main.java.com.logger.sink.*;
import main.java.com.logger.sink.ConsoleSink;
import main.java.com.logger.sink.DatabaseSink;
import main.java.com.logger.sink.FileSink;
import main.java.com.logger.sink.Sink;
import main.java.com.logger.threading.ThreadModel;
import main.java.com.logger.threading.WriteMode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class ConfigParser {

    public static LoggerConfiguration parseFromFile(String configFilePath) throws IOException {
        Map<String, String> properties = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    properties.put(parts[0].trim(), parts[1].trim());
                }
            }
        }

        return parseFromProperties(properties);
    }

    public static LoggerConfiguration parseFromProperties(Map<String, String> properties) {
        LoggerConfiguration config = new LoggerConfiguration();

        // Parse global log level
        String logLevel = properties.get("log_level");
        if (logLevel != null) {
            config.setGlobalLogLevel(LogLevel.valueOf(logLevel));
        }

        // Parse thread model
        String threadModel = properties.get("thread_model");
        if (threadModel != null) {
            config.setThreadModel(ThreadModel.valueOf(threadModel));
        }

        // Parse write mode
        String writeMode = properties.get("write_mode");
        if (writeMode != null) {
            config.setWriteMode(WriteMode.valueOf(writeMode));
        }

        // Set default hostname
        try {
            config.setDefaultHostName(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            config.setDefaultHostName("unknown-host");
        }

        // Create and configure sink
        String sinkType = properties.get("sink_type");
        if (sinkType != null) {
            Sink sink = createSink(sinkType);
            SinkConfiguration sinkConfig = createSinkConfiguration(properties);
            sink.configure(sinkConfig);

            // Map sink to the specified log level
            LogLevel level = config.getGlobalLogLevel();
            config.addSink(level, sink);

            // Also map higher priority levels to the same sink
            for (LogLevel l : LogLevel.values()) {
                if (l.getPriority() >= level.getPriority()) {
                    config.addSink(l, sink);
                }
            }
        }

        return config;
    }

    private static Sink createSink(String sinkType) {
        switch (sinkType.toLowerCase()) {
            case "file":
                return new FileSink();
            case "console":
                return new ConsoleSink();
            case "db":
                return new DatabaseSink();
            default:
                throw new IllegalArgumentException("Unknown sink type: " + sinkType);
        }
    }

    private static SinkConfiguration createSinkConfiguration(Map<String, String> properties) {
        SinkConfiguration config = new SinkConfiguration();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            config.setProperty(entry.getKey(), entry.getValue());
        }
        return config;
    }
}
