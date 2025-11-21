package com.payment.app.infrastructure.messaging.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.app.application.dto.CreatePaymentEvent;
import com.payment.app.application.port.out.PaymentEventPublisherPort;
import com.payment.app.infrastructure.config.RabbitMQConfig;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
public class PaymentEventPublisherPortImpl implements PaymentEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public PaymentEventPublisherPortImpl(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishPaymentCreatedEvent(CreatePaymentEvent event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.MAIN_EXCHANGE_NAME,
                    RabbitMQConfig.MAIN_QUEUE_NAME,
                    jsonEvent
            );
        }catch (AmqpException e) {
            throw new RuntimeException("Failed to publish event to RabbitMQ", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
