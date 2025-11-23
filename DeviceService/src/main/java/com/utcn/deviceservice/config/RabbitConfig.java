package com.utcn.deviceservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String DEVICE_EVENTS_QUEUE = "device.events.queue";

    @Bean
    public Queue deviceEventsQueue() {
        return new Queue(DEVICE_EVENTS_QUEUE, true);
    }
}