package com.utcn.monitorservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin; // Import this
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitConfig {

    @Value("${app.queue.name}")
    private String sensorQueueName;

    @Value("${internal.queue.name}")
    private String internalQueueName;

    // ========================================================================
    // BROKER 1: SENSOR DATA
    // ========================================================================
    @Primary
    @Bean(name = "sensorConnectionFactory")
    public ConnectionFactory sensorConnectionFactory(
            @Value("${sensor.rabbitmq.host}") String host,
            @Value("${sensor.rabbitmq.port}") int port,
            @Value("${sensor.rabbitmq.username}") String username,
            @Value("${sensor.rabbitmq.password}") String password) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Primary
    @Bean(name = "sensorRabbitTemplate")
    public RabbitTemplate sensorRabbitTemplate(@Qualifier("sensorConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Primary
    @Bean(name = "sensorContainerFactory")
    public SimpleRabbitListenerContainerFactory sensorContainerFactory(
            @Qualifier("sensorConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean(name = "sensorAdmin")
    public RabbitAdmin sensorAdmin(@Qualifier("sensorConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue sensorDataQueue(@Qualifier("sensorAdmin") RabbitAdmin sensorAdmin) {
        Queue queue = new Queue(sensorQueueName, true);
        queue.setAdminsThatShouldDeclare(sensorAdmin);
        return queue;
    }

    // ========================================================================
    // BROKER 2: INTERNAL EVENTS
    // ========================================================================
    @Bean(name = "internalConnectionFactory")
    public ConnectionFactory internalConnectionFactory(
            @Value("${internal.rabbitmq.host}") String host,
            @Value("${internal.rabbitmq.port}") int port,
            @Value("${internal.rabbitmq.username}") String username,
            @Value("${internal.rabbitmq.password}") String password) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean(name = "internalRabbitTemplate")
    public RabbitTemplate internalRabbitTemplate(@Qualifier("internalConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean(name = "internalContainerFactory")
    public SimpleRabbitListenerContainerFactory internalContainerFactory(
            @Qualifier("internalConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean(name = "internalAdmin")
    public RabbitAdmin internalAdmin(@Qualifier("internalConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue deviceEventsQueue(@Qualifier("internalAdmin") RabbitAdmin internalAdmin) {
        Queue queue = new Queue(internalQueueName, true);
        queue.setAdminsThatShouldDeclare(internalAdmin); // This ensures it is created on the correct broker
        return queue;
    }
}