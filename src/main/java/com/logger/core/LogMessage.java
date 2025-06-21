package main.java.com.logger.core;

import java.time.LocalDateTime;

public class LogMessage {
    private final String content;
    private final LogLevel level;
    private final String namespace;
    private final LocalDateTime timestamp;
    private final String trackingId;
    private final String hostName;

    private LogMessage(Builder builder) {
        this.content = builder.content;
        this.level = builder.level;
        this.namespace = builder.namespace;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
        this.trackingId = builder.trackingId;
        this.hostName = builder.hostName;
    }

    public String getContent() { return content; }
    public LogLevel getLevel() { return level; }
    public String getNamespace() { return namespace; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getTrackingId() { return trackingId; }
    public String getHostName() { return hostName; }

    public static class Builder {
        private String content;
        private LogLevel level;
        private String namespace;
        private LocalDateTime timestamp;
        private String trackingId;
        private String hostName;

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder level(LogLevel level) {
            this.level = level;
            return this;
        }

        public Builder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder trackingId(String trackingId) {
            this.trackingId = trackingId;
            return this;
        }

        public Builder hostName(String hostName) {
            this.hostName = hostName;
            return this;
        }

        public LogMessage build() {
            if (content == null || level == null || namespace == null) {
                throw new IllegalArgumentException("Content, level, and namespace are required");
            }
            return new LogMessage(this);
        }
    }
}