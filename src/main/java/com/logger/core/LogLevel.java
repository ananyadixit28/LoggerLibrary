package main.java.com.logger.core;

// setting log level priority wise, in future we can use chain of responsibility to add further priority change
public enum LogLevel {
    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3),
    FATAL(4);

    private final int priority;

    LogLevel(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public boolean shouldLog(LogLevel configuredLevel) {
        return this.priority >= configuredLevel.priority;
    }
}