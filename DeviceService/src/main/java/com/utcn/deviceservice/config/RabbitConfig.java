package com.utcn.deviceservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String INTERNAL_EXCHANGE = "internal.exchange";

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(INTERNAL_EXCHANGE);
    }
}