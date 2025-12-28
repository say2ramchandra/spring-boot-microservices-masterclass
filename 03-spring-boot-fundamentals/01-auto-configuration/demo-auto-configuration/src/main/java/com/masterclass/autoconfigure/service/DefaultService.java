package com.masterclass.autoconfigure.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default service implementation created when no custom service is defined.
 * 
 * This demonstrates @ConditionalOnMissingBean annotation.
 */
public class DefaultService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultService.class);

    public DefaultService() {
        logger.info("DefaultService initialized (no custom service provided)");
    }

    public String performOperation() {
        logger.info("Performing default operation");
        return "Default operation completed";
    }
}
