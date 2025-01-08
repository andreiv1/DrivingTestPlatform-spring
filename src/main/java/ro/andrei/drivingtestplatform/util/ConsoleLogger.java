package ro.andrei.drivingtestplatform.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class ConsoleLogger extends Logger{

    protected ConsoleLogger() {}

    @Override
    public void info(String message) {
        log("INFO", message);
    }

    @Override
    public void warn(String message) {
        log("WARN", message);
    }

    @Override
    public void error(String message) {
        log("ERROR", message);
    }

    private void log(String level, String message) {
        String timestamp = java.time.LocalDateTime.now().toString();
        System.out.printf("[%s] [%s] %s%n", timestamp, level, message);
    }
}

