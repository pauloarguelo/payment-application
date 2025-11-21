package com.payment.app.infrastructure.messaging.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.app.application.dto.PaymentWebhookProcessEvent;
import com.payment.app.application.port.out.PaymentWebhookEventPublisherPort;
import com.payment.app.infrastructure.messaging.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentWebhookEventPublisherPortImpl implements PaymentWebhookEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(PaymentWebhookEventPublisherPortImpl.class);

    public PaymentWebhookEventPublisherPortImpl(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishPaymentWebhookProcessEvent(PaymentWebhookProcessEvent event) {

        logger.info("PaymentWebhookEventPublisherPortImpl: Publishing PaymentWebhookProcessEvent: {}", event);

        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.DELIVERY_EXCHANGE_NAME,
                    RabbitMQConfig.DELIVERY_QUEUE_NAME,
                    jsonEvent
            );
        }catch (AmqpException e) {

            logger.warn("PaymentWebhookEventPublisherPortImpl: Failed to publish event to RabbitMQ: {}", e.getMessage());
            throw new RuntimeException("Failed to publish event to RabbitMQ", e);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
