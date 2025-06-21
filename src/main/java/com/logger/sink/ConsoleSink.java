package main.java.com.logger.sink;

import main.java.com.logger.core.LogMessage;
import main.java.com.logger.config.SinkConfiguration;

import java.time.format.DateTimeFormatter;

public class ConsoleSink implements Sink {
    private String timestampFormat;

    @Override
    public void configure(SinkConfiguration config) {
        this.timestampFormat = config.getProperty("ts_format", "dd:MM:yyyy HH:mm:ss");
    }

    @Override
    public void write(LogMessage message) {
        String formattedMessage = formatMessage(message);
        System.out.println(formattedMessage);
    }

    private String formatMessage(LogMessage message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm:ss");
        String timestamp = message.getTimestamp().format(formatter);

        StringBuilder sb = new StringBuilder();
        sb.append(message.getLevel()).append(" [").append(timestamp).append("]");

        if (message.getNamespace() != null) {
            sb.append(" [").append(message.getNamespace()).append("]");
        }

        if (message.getTrackingId() != null) {
            sb.append(" [").append(message.getTrackingId()).append("]");
        }

        if (message.getHostName() != null) {
            sb.append(" [").append(message.getHostName()).append("]");
        }

        sb.append(" ").append(message.getContent());

        return sb.toString();
    }

    @Override
    public void close() {
    }

    @Override
    public String getType() {
        return "CONSOLE";
    }
}