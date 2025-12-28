package com.example.configclient.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Configuration Change Listener
 * 
 * Listens for configuration refresh events and logs changes.
 * Useful for monitoring and debugging configuration updates.
 * 
 * EnvironmentChangeEvent is fired when:
 * - POST /actuator/refresh is called
 * - Configuration is updated via Spring Cloud Bus
 */
@Component
public class ConfigChangeListener {

    private static final Logger log = LoggerFactory.getLogger(ConfigChangeListener.class);

    /**
     * Handle configuration change event
     * 
     * This method is called whenever configuration is refreshed.
     * 
     * @param event Contains set of keys that were changed
     */
    @EventListener
    public void handleConfigChange(EnvironmentChangeEvent event) {
        log.info("=".repeat(70));
        log.info("Configuration Refresh Event Received");
        log.info("=".repeat(70));
        
        log.info("Changed Properties:");
        event.getKeys().forEach(key -> {
            log.info("  - {}", key);
        });
        
        log.info("Total properties changed: {}", event.getKeys().size());
        log.info("=".repeat(70));
    }
}
