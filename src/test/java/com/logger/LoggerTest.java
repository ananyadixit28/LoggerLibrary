package test.java.com.logger;


import main.java.com.logger.LoggerFactory;
import main.java.com.logger.core.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LoggerTest {

    public static void main(String[] args) {
        testFileLogger();
        testConsoleLogger();
    }

    private static void testFileLogger() {
        System.out.println("Testing File Logger...");

        Map<String, String> config = new HashMap<>();
        config.put("ts_format", "dd:MM:yyyy HH:mm:ss");
        config.put("log_level", "INFO");
        config.put("sink_type", "FILE");
        String logPath = System.getProperty("java.io.tmpdir") + "logs" + File.separator + "application.log";
        config.put("file_location", logPath);
        config.put("thread_model", "SINGLE");
        config.put("write_mode", "SYNC");
        config.put("max_file_size", "1048"); // 0.1 MB
        config.put("max_backup_files", "3");

        Logger logger = LoggerFactory.createLogger(config, "com.example.app");

        logger.info("Application started successfully");
        logger.warn("This is a warning message", "com.example.service");
        logger.error("An error occurred while processing request");
        logger.debug("This debug message won't be logged due to log level");

        logger.close();
        System.out.println("File logger test completed. Check " + logPath);
    }

    private static void testConsoleLogger() {
        System.out.println("\nTesting Console Logger...");

        Map<String, String> config = new HashMap<>();
        config.put("ts_format", "dd:MM:yyyy HH:mm:ss");
        config.put("log_level", "DEBUG");
        config.put("sink_type", "CONSOLE");
        config.put("thread_model", "SINGLE");
        config.put("write_mode", "SYNC");

        Logger logger = LoggerFactory.createLogger(config, "com.example.console");

        logger.debug("Debug message for console");
        logger.info("Info message for console");
        logger.warn("Warning message for console");
        logger.error("Error message for console");
        logger.fatal("Fatal message for console");

        logger.close();
        System.out.println("Console logger test completed.");
    }
}
