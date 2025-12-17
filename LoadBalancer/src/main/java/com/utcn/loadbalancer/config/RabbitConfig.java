package com.utcn.loadbalancer.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class RabbitConfig {

    public static final String INPUT_QUEUE = "sensor.data.queue";
    public static final String OUTPUT_QUEUE_PREFIX = "monitor.queue.";

    @Value("${monitor.instances.count:1}")
    private int monitorInstancesCount;

    private final AmqpAdmin amqpAdmin;

    public RabbitConfig(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    @PostConstruct
    public void createQueues() {
        amqpAdmin.declareQueue(new Queue(INPUT_QUEUE, true));

        for (int i = 0; i < monitorInstancesCount; i++) {
            String queueName = OUTPUT_QUEUE_PREFIX + i;
            amqpAdmin.declareQueue(new Queue(queueName, true));
            System.out.println(">>> LoadBalancer: Declared queue " + queueName);
        }
    }
}