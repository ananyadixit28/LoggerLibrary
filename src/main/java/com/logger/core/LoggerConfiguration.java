package main.java.com.logger.core;

import main.java.com.logger.sink.Sink;
import main.java.com.logger.threading.ThreadModel;
import main.java.com.logger.threading.WriteMode;

import java.util.HashMap;
import java.util.Map;

public class LoggerConfiguration {
    private final Map<LogLevel, Sink> levelSinkMapping;
    private LogLevel globalLogLevel;
    private ThreadModel threadModel;
    private WriteMode writeMode;
    private String defaultTrackingId;
    private String defaultHostName;

    public LoggerConfiguration() {
        this.levelSinkMapping = new HashMap<>();
        this.globalLogLevel = LogLevel.INFO;
        this.threadModel = ThreadModel.SINGLE;
        this.writeMode = WriteMode.SYNC;
    }

    public void addSink(LogLevel level, Sink sink) {
        levelSinkMapping.put(level, sink);
    }

    public Sink getSink(LogLevel level) {
        return levelSinkMapping.get(level);
    }

    public LogLevel getGlobalLogLevel() { return globalLogLevel; }
    public void setGlobalLogLevel(LogLevel globalLogLevel) { this.globalLogLevel = globalLogLevel; }

    public ThreadModel getThreadModel() { return threadModel; }
    public void setThreadModel(ThreadModel threadModel) { this.threadModel = threadModel; }

    public WriteMode getWriteMode() { return writeMode; }
    public void setWriteMode(WriteMode writeMode) { this.writeMode = writeMode; }

    public String getDefaultTrackingId() { return defaultTrackingId; }
    public void setDefaultTrackingId(String defaultTrackingId) { this.defaultTrackingId = defaultTrackingId; }

    public String getDefaultHostName() { return defaultHostName; }
    public void setDefaultHostName(String defaultHostName) { this.defaultHostName = defaultHostName; }
}