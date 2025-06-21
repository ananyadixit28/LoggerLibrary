# Logger Library

A simple, extensible logging library for Java applications.
This library allows applications to log messages to various sinks such as console or file. It supports configurable log levels, automatic log rotation with compression, and pluggable sink architecture for easy extensibility.

---

## Features

- Supports log levels: `DEBUG`, `INFO`, `WARN`, `ERROR`, `FATAL`
- Enriches log messages with timestamp, namespace, trackingId, and hostname
- Supports console and file sinks out of the box
- Automatic log rotation and compression for file-based logs
- Thread-safe logging with support for synchronous and asynchronous modes
- Extensible sink architecture – implement your own sinks (e.g., DB, Kafka, etc.)

## Quick Start

```java
// 1. Create configuration
Map<String, String> config = new HashMap<>();
config.put("log_level", "INFO");
config.put("sink_type", "FILE");
config.put("file_location", "logs/app.log");

// 2. Create logger
Logger logger = LoggerFactory.createLogger(config, "MyApp");

// 3. Log messages
logger.info("Application started");
logger.warn("Warning message");
logger.error("Error occurred");

// 4. Close logger
logger.close();
```

## Configuration Options

| Property | Values | Description |
|----------|--------|-------------|
| log_level | DEBUG, INFO, WARN, ERROR, FATAL | Minimum log level to show |
| sink_type | FILE, CONSOLE, DB | Where to send logs |
| file_location | Path string | Log file path (for FILE sink) |
| write_mode | SYNC, ASYNC | Write synchronously or asynchronously |

## Supported Sinks

- **FILE**: Writes to log files with auto-rotation
- **CONSOLE**: Writes to console output  
- **DB**: Writes to MySQL database

## Log Format

```
INFO [21:06:2025 14:30:15] [MyApp] [abc123] [hostname] Your log message here
```

## File Rotation

Files automatically rotate when they get too large:
- app.log (current file)
- app.log.1.gz (previous file, compressed)
- app.log.2.gz (older file, compressed)

## Running Tests

```bash
javac com/logger/**/*.java
java com.logger.LoggerTest
```

## Project Structure

```
src/main/java/com/logger/
├── core/           # Main logger classes
├── sink/           # Different output destinations
├── config/         # Configuration handling
├── rotation/       # File rotation logic
└── LoggerFactory.java
```
