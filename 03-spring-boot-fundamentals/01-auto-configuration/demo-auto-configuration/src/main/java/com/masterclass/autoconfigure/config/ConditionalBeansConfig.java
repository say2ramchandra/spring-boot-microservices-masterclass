package com.masterclass.autoconfigure.config;

import com.masterclass.autoconfigure.service.DatabaseService;
import com.masterclass.autoconfigure.service.DefaultService;
import com.masterclass.autoconfigure.service.EmailService;
import com.masterclass.autoconfigure.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration demonstrating various conditional annotations.
 * 
 * Each bean is created only when specific conditions are met:
 * - EmailService: When feature.email.enabled=true
 * - NotificationService: When feature.notification.enabled=true
 * - DatabaseService: When H2 database class is on classpath
 * - DefaultService: When no custom service bean is defined
 */
@Configuration
public class ConditionalBeansConfig {

    private static final Logger logger = LoggerFactory.getLogger(ConditionalBeansConfig.class);

    /**
     * EmailService is created only when feature.email.enabled=true.
     * 
     * matchIfMissing=false means if the property is not defined, bean is NOT created.
     */
    @Bean
    @ConditionalOnProperty(
        name = "feature.email.enabled",
        havingValue = "true",
        matchIfMissing = false
    )
    public EmailService emailService() {
        logger.info("Creating EmailService bean (feature.email.enabled=true)");
        return new EmailService();
    }

    /**
     * NotificationService is created only when feature.notification.enabled=true.
     * 
     * This demonstrates feature toggles using configuration properties.
     */
    @Bean
    @ConditionalOnProperty(
        name = "feature.notification.enabled",
        havingValue = "true",
        matchIfMissing = false
    )
    public NotificationService notificationService() {
        logger.info("Creating NotificationService bean (feature.notification.enabled=true)");
        return new NotificationService();
    }

    /**
     * DatabaseService is created only when H2 database is on the classpath.
     * 
     * @ConditionalOnClass checks if specified class exists.
     * If H2 dependency is removed, this bean won't be created.
     */
    @Bean
    @ConditionalOnClass(name = "org.h2.Driver")
    public DatabaseService databaseService() {
        logger.info("Creating DatabaseService bean (H2 driver found on classpath)");
        return new DatabaseService();
    }

    /**
     * DefaultService is created only if no other service bean exists.
     * 
     * @ConditionalOnMissingBean provides a fallback/default implementation
     * that can be overridden by user-defined beans.
     */
    @Bean
    @ConditionalOnMissingBean(name = "customDefaultService")
    public DefaultService defaultService() {
        logger.info("Creating DefaultService bean (no custom service defined)");
        return new DefaultService();
    }

    /**
     * Example of @ConditionalOnBean - creates bean only if another bean exists.
     * Commented out to avoid circular dependency in demo.
     */
    /*
    @Bean
    @ConditionalOnBean(EmailService.class)
    public EmailValidator emailValidator() {
        logger.info("Creating EmailValidator bean (EmailService exists)");
        return new EmailValidator();
    }
    */

    /**
     * Example of @ConditionalOnWebApplication - only in web context.
     * Commented out because this is not a web application.
     */
    /*
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public WebFilter webFilter() {
        logger.info("Creating WebFilter bean (servlet web application)");
        return new WebFilter();
    }
    */
}
