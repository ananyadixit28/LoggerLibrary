package main.java.com.logger.sink;


import main.java.com.logger.config.SinkConfiguration;
import main.java.com.logger.core.LogMessage;
import main.java.com.logger.rotation.FileRotationManager;
import main.java.com.logger.threading.WriteMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileSink implements Sink {
    private String filePath;
    private String timestampFormat;
    private WriteMode writeMode;
    private BufferedWriter writer;
    private FileRotationManager rotationManager;
    private BlockingQueue<LogMessage> messageQueue;
    private Thread asyncWriterThread;
    private AtomicBoolean shutdown = new AtomicBoolean(false);

    @Override
    public void configure(SinkConfiguration config) {
        this.filePath = config.getProperty("file_location");
        this.timestampFormat = config.getProperty("ts_format", "ddMMyyyyHHmmss");
        this.writeMode = WriteMode.valueOf(config.getProperty("write_mode", "SYNC"));

        long maxFileSize = Long.parseLong(config.getProperty("max_file_size", "10485760"));
        int maxBackupFiles = Integer.parseInt(config.getProperty("max_backup_files", "5"));

        this.rotationManager = new FileRotationManager(filePath, maxFileSize, maxBackupFiles);

        try {
            initializeWriter();
            if (writeMode == WriteMode.ASYNC) {
                initializeAsyncWriter();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize file sink", e);
        }
    }

    private void initializeWriter() throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }

        this.writer = new BufferedWriter(new FileWriter(filePath, true));
    }

    private void initializeAsyncWriter() {
        this.messageQueue = new LinkedBlockingQueue<>();
        this.asyncWriterThread = new Thread(this::processAsyncMessages);
        this.asyncWriterThread.setDaemon(true);
        this.asyncWriterThread.start();
    }

    @Override
    public void write(LogMessage message) {
        if (writeMode == WriteMode.ASYNC) {
            try {
                messageQueue.put(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Failed to queue message", e);
            }
        } else {
            writeMessageSync(message);
        }
    }

    private void processAsyncMessages() {
        while (!shutdown.get() || !messageQueue.isEmpty()) {
            try {
                LogMessage message = messageQueue.take();
                writeMessageSync(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private synchronized void writeMessageSync(LogMessage message) {
        try {
            if (rotationManager.shouldRotate()) {
                writer.close();
                rotationManager.rotateFile();
                initializeWriter();
            }

            String formattedMessage = formatMessage(message);
            writer.write(formattedMessage);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write log message", e);
        }
    }

    private String formatMessage(LogMessage message) {
        DateTimeFormatter formatter = createDateTimeFormatter(timestampFormat);
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

    private DateTimeFormatter createDateTimeFormatter(String format) {
        String pattern = format
                .replace("dd", "dd")
                .replace("mm", "MM")
                .replace("yyyy", "yyyy")
                .replace("hh", "HH")
                .replace("mm", "mm")
                .replace("ss", "ss");

        return DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm:ss");
    }

    @Override
    public void close() {
        shutdown.set(true);

        if (asyncWriterThread != null) {
            try {
                asyncWriterThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing file sink: " + e.getMessage());
        }
    }

    @Override
    public String getType() {
        return "FILE";
    }
}
