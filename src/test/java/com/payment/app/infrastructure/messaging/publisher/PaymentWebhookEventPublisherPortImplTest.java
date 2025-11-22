package com.payment.app.infrastructure.messaging.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.app.application.dto.PaymentWebhookProcessEvent;
import com.payment.app.infrastructure.config.RabbitMQConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentWebhookEventPublisherPortImpl Tests")
class PaymentWebhookEventPublisherPortImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentWebhookEventPublisherPortImpl publisher;

    private PaymentWebhookProcessEvent event;
    private String jsonEvent;

    @BeforeEach
    void setUp() {
        UUID paymentId = UUID.randomUUID();
        UUID webhookId = UUID.randomUUID();
        event = new PaymentWebhookProcessEvent(paymentId, webhookId);
        jsonEvent = "{\"paymentId\":\"" + paymentId + "\",\"webhookId\":\"" + webhookId + "\"}";
    }

    @Test
    @DisplayName("Should publish event successfully")
    void publishPaymentWebhookProcessEvent_shouldPublishSuccessfully() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenReturn(jsonEvent);

        publisher.publishPaymentWebhookProcessEvent(event);

        verify(objectMapper).writeValueAsString(event);
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.DELIVERY_EXCHANGE_NAME),
                eq(RabbitMQConfig.DELIVERY_QUEUE_NAME),
                eq(jsonEvent)
        );
    }

    @Test
    @DisplayName("Should throw exception when RabbitMQ fails")
    void publishPaymentWebhookProcessEvent_shouldThrowException_whenRabbitMQFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenReturn(jsonEvent);
        doThrow(new AmqpException("RabbitMQ connection failed"))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), anyString());

        assertThatThrownBy(() -> publisher.publishPaymentWebhookProcessEvent(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to publish event to RabbitMQ")
                .hasCauseInstanceOf(AmqpException.class);

        verify(objectMapper).writeValueAsString(event);
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.DELIVERY_EXCHANGE_NAME),
                eq(RabbitMQConfig.DELIVERY_QUEUE_NAME),
                eq(jsonEvent)
        );
    }

    @Test
    @DisplayName("Should throw exception when JSON serialization fails")
    void publishPaymentWebhookProcessEvent_shouldThrowException_whenJsonSerializationFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event))
                .thenThrow(new JsonProcessingException("Serialization error") {});

        assertThatThrownBy(() -> publisher.publishPaymentWebhookProcessEvent(event))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(JsonProcessingException.class);

        verify(objectMapper).writeValueAsString(event);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
    }
}

