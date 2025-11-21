package com.payment.app.infrastructure.messaging;

import com.payment.app.application.port.out.EventPublisherPort;
import com.payment.app.infrastructure.messaging.config.RabbitMQConfig;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static org.springframework.amqp.core.Binding.DestinationType.EXCHANGE;


@Component
public class EventPublisherPortImpl implements EventPublisherPort {


    private final RabbitTemplate rabbitTemplate;


    public EventPublisherPortImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    public boolean publishPaymentCreatedEvent(String paymentDetailsJson) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.MAIN_EXCHANGE_NAME,
                    RabbitMQConfig.WEBHOOK_QUEUE_NAME,
                    paymentDetailsJson
            );
            return true;
        }catch (AmqpException e) {
            throw new RuntimeException("Failed to publish event to RabbitMQ", e);
        }
    }
}
