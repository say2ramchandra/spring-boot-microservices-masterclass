package com.masterclass.autoconfigure.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database service that is conditionally created when H2 is on classpath.
 * 
 * This demonstrates @ConditionalOnClass annotation.
 */
public class DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    public DatabaseService() {
        logger.info("DatabaseService initialized (H2 driver available)");
    }

    public void query(String sql) {
        logger.info("Executing SQL query: {}", sql);
    }
}
