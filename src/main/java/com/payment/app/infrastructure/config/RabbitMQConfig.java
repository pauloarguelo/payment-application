package com.payment.app.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String DLQ_QUEUE_NAME = "webhooks.dlq";
    public static final String DLQ_EXCHANGE_NAME = "webhooks.dlq.exchange";

    public static final String MAIN_QUEUE_NAME = "webhooks.main";
    public static final String MAIN_EXCHANGE_NAME = "webhooks.main.exchange";

    public static final String DELIVERY_QUEUE_NAME = "webhooks.delivery";
    public static final String DELIVERY_EXCHANGE_NAME = "webhooks.delivery.exchange";

    private final RabbitMQProperties properties;

    public RabbitMQConfig(RabbitMQProperties properties) {
        this.properties = properties;
    }
    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(DLQ_EXCHANGE_NAME);
    }

    @Bean
    public Queue dlqQueue() {
        return new Queue(DLQ_QUEUE_NAME);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue()).to(dlqExchange()).with(DLQ_QUEUE_NAME);
    }

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(MAIN_EXCHANGE_NAME);
    }

    @Bean
    public Queue mainQueue() {
        return new Queue(MAIN_QUEUE_NAME, true, false, false,
                Map.of(
                        "x-dead-letter-exchange", DLQ_EXCHANGE_NAME,
                        "x-dead-letter-routing-key", DLQ_QUEUE_NAME
                )
        );
    }

    @Bean
    public Binding mainBinding() {
        return BindingBuilder.bind(mainQueue()).to(mainExchange()).with(MAIN_QUEUE_NAME);
    }

    @Bean
    public DirectExchange deliveryExchange() {
        return new DirectExchange(DELIVERY_EXCHANGE_NAME);
    }

    @Bean
    public Queue deliveryQueue() {
        return new Queue(DELIVERY_QUEUE_NAME, true, false, false,
                Map.of(
                        "x-dead-letter-exchange", DLQ_EXCHANGE_NAME,
                        "x-dead-letter-routing-key", DLQ_QUEUE_NAME
                )
        );
    }

    @Bean
    public Binding deliveryBinding() {
        return BindingBuilder.bind(deliveryQueue()).to(deliveryExchange()).with(DELIVERY_QUEUE_NAME);
    }


    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        if (properties.getRetry().isEnabled()) {
            factory.setAdviceChain(retryInterceptor());
        }

        factory.setConcurrentConsumers(properties.getConcurrency());
        factory.setMaxConcurrentConsumers(properties.getMaxConcurrency());

        return factory;
    }


    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        RabbitMQProperties.RetryConfig retryConfig = properties.getRetry();

        return RetryInterceptorBuilder.stateless()
                .maxAttempts(retryConfig.getMaxAttempts())
                .backOffOptions(
                        retryConfig.getInitialInterval(),
                        retryConfig.getMultiplier(),
                        retryConfig.getMaxInterval()
                )
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }
}