package main.java.com.logger.core;

import main.java.com.logger.sink.Sink;

import java.util.UUID;

public class Logger {
    private final LoggerConfiguration configuration;
    private final String defaultNamespace;

    public Logger(LoggerConfiguration configuration, String defaultNamespace) {
        this.configuration = configuration;
        this.defaultNamespace = defaultNamespace;
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message, defaultNamespace);
    }

    public void debug(String message, String namespace) {
        log(LogLevel.DEBUG, message, namespace);
    }

    public void info(String message) {
        log(LogLevel.INFO, message, defaultNamespace);
    }

    public void info(String message, String namespace) {
        log(LogLevel.INFO, message, namespace);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message, defaultNamespace);
    }

    public void warn(String message, String namespace) {
        log(LogLevel.WARN, message, namespace);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message, defaultNamespace);
    }

    public void error(String message, String namespace) {
        log(LogLevel.ERROR, message, namespace);
    }

    public void fatal(String message) {
        log(LogLevel.FATAL, message, defaultNamespace);
    }

    public void fatal(String message, String namespace) {
        log(LogLevel.FATAL, message, namespace);
    }

    public void log(LogLevel level, String content, String namespace) {
        log(level, content, namespace, null);
    }

    public void log(LogLevel level, String content, String namespace, String trackingId) {
        if (!level.shouldLog(configuration.getGlobalLogLevel())) {
            return;
        }

        String actualTrackingId = trackingId != null ? trackingId :
                configuration.getDefaultTrackingId() != null ?
                        configuration.getDefaultTrackingId() :
                        UUID.randomUUID().toString().substring(0, 8);

        LogMessage message = new LogMessage.Builder()
                .content(content)
                .level(level)
                .namespace(namespace)
                .trackingId(actualTrackingId)
                .hostName(configuration.getDefaultHostName())
                .build();

        Sink sink = configuration.getSink(level);
        if (sink != null) {
            sink.write(message);
        }
    }

    public void close() {
        // Close all sinks
        for (LogLevel level : LogLevel.values()) {
            Sink sink = configuration.getSink(level);
            if (sink != null) {
                sink.close();
            }
        }
    }
}