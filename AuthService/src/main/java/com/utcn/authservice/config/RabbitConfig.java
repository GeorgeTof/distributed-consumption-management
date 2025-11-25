package com.utcn.authservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${internal.exchange.name}")
    private String internalExchangeName;

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(internalExchangeName);
    }
}