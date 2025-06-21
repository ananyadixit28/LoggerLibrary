package main.java.com.logger.sink;

import main.java.com.logger.core.LogMessage;
import main.java.com.logger.config.SinkConfiguration;

public interface Sink {
    void write(LogMessage message);
    void configure(SinkConfiguration config);
    void close();
    String getType();
}