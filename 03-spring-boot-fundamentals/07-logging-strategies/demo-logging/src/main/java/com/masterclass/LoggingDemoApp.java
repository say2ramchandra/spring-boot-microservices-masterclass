package com.masterclass;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Logging Strategies Demo Application
 * 
 * Demonstrates:
 * - SLF4J logging with Logback
 * - Log levels (TRACE, DEBUG, INFO, WARN, ERROR)
 * - Custom logback configuration
 * - MDC (Mapped Diagnostic Context)
 * - Structured JSON logging
 * - Best logging practices
 */
@SpringBootApplication
@Slf4j
public class LoggingDemoApp {

    public static void main(String[] args) {
        log.info("Starting Logging Demo Application...");
        
        SpringApplication.run(LoggingDemoApp.class, args);
        
        log.info("\n" +
            "==============================================\n" +
            "🚀 Logging Demo Started Successfully!\n" +
            "==============================================\n" +
            "📡 API: http://localhost:8080/api/demo\n" +
            "📊 H2 Console: http://localhost:8080/h2-console\n" +
            "\n" +
            "📁 Log Files:\n" +
            "  - logs/application.log (all logs)\n" +
            "  - logs/error.log (errors only)\n" +
            "  - logs/application.json (JSON format)\n" +
            "\n" +
            "🎯 Try these endpoints:\n" +
            "  GET  /api/demo/all-levels     → See all log levels\n" +
            "  GET  /api/demo/with-params    → Parameterized logging\n" +
            "  GET  /api/demo/with-exception → Exception logging\n" +
            "  GET  /api/demo/with-mdc       → MDC context logging\n" +
            "==============================================");
    }
}
