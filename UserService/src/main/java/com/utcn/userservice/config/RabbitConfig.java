package com.utcn.userservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${internal.exchange.name}")
    private String internalExchangeName;

    @Value("${internal.queue.name}")
    private String internalQueueName;

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(internalExchangeName);
    }

    @Bean
    public Queue userSyncQueue() {
        return new Queue(internalQueueName, true);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(userSyncQueue())
                .to(internalExchange())
                .with("user.#");
    }
}