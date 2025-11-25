package com.utcn.deviceservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String INTERNAL_EXCHANGE = "internal.exchange";

    public static final String USER_SYNC_QUEUE = "device.user.sync.queue";

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(INTERNAL_EXCHANGE);
    }

    @Bean
    public Queue userSyncQueue() {
        return new Queue(USER_SYNC_QUEUE, true);
    }

    @Bean
    public Binding userBinding() {
        // Bind to "user.#" to get all user events (created, deleted)
        return BindingBuilder
                .bind(userSyncQueue())
                .to(internalExchange())
                .with("user.#");
    }
}