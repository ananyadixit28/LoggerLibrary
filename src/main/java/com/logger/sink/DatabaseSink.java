package main.java.com.logger.sink;

import main.java.com.logger.core.LogMessage;
import main.java.com.logger.config.SinkConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DatabaseSink implements Sink {
    private Connection connection;
    private PreparedStatement insertStatement;

    @Override
    public void configure(SinkConfiguration config) {
        String host = config.getProperty("dbhost");
        String port = config.getProperty("dbport");
        String database = config.getProperty("database", "logs");
        String username = config.getProperty("username");
        String password = config.getProperty("password");

        try {
            String url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
            this.connection = DriverManager.getConnection(url, username, password);

            String sql = "INSERT INTO logs (level, timestamp, namespace, content, tracking_id, host_name) VALUES (?, ?, ?, ?, ?, ?)";
            this.insertStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database sink", e);
        }
    }

    @Override
    public void write(LogMessage message) {
        try {
            insertStatement.setString(1, message.getLevel().toString());
            insertStatement.setTimestamp(2, Timestamp.valueOf(message.getTimestamp()));
            insertStatement.setString(3, message.getNamespace());
            insertStatement.setString(4, message.getContent());
            insertStatement.setString(5, message.getTrackingId());
            insertStatement.setString(6, message.getHostName());

            insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to write log message to database", e);
        }
    }

    @Override
    public void close() {
        try {
            if (insertStatement != null) insertStatement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.err.println("Error closing database sink: " + e.getMessage());
        }
    }

    @Override
    public String getType() {
        return "DB";
    }
}