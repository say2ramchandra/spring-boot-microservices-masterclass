package com.masterclass.messaging.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 * 
 * Defines exchanges, queues, and bindings for different messaging patterns.
 */
@Configuration
public class RabbitMQConfig {

    // Queue names
    public static final String ORDER_QUEUE = "order.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String EMAIL_QUEUE = "email.queue";
    public static final String SMS_QUEUE = "sms.queue";

    // Exchange names
    public static final String DIRECT_EXCHANGE = "direct.exchange";
    public static final String FANOUT_EXCHANGE = "fanout.exchange";
    public static final String TOPIC_EXCHANGE = "topic.exchange";

    // Routing keys
    public static final String ORDER_ROUTING_KEY = "order.created";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.send";

    // ====================
    // DIRECT EXCHANGE (One-to-One)
    // ====================
    
    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true); // durable
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder
                .bind(orderQueue())
                .to(directExchange())
                .with(ORDER_ROUTING_KEY);
    }

    // ====================
    // FANOUT EXCHANGE (Broadcast)
    // ====================
    
    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Queue smsQueue() {
        return new Queue(SMS_QUEUE, true);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder
                .bind(emailQueue())
                .to(fanoutExchange());
    }

    @Bean
    public Binding smsBinding() {
        return BindingBuilder
                .bind(smsQueue())
                .to(fanoutExchange());
    }

    // ====================
    // TOPIC EXCHANGE (Pattern Matching)
    // ====================
    
    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(topicExchange())
                .with("notification.*"); // Wildcard pattern
    }
}
