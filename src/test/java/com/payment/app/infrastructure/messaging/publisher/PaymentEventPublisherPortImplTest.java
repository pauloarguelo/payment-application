package com.payment.app.infrastructure.messaging.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.app.application.dto.CreatePaymentEvent;
import com.payment.app.domain.type.PaymentStatus;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentEventPublisherPortImpl Tests")
class PaymentEventPublisherPortImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentEventPublisherPortImpl publisher;

    private CreatePaymentEvent event;
    private String jsonEvent;
    private UUID paymentId;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        event = new CreatePaymentEvent(paymentId, PaymentStatus.CREATED);
        jsonEvent = "{\"paymentId\":\"" + paymentId + "\",\"status\":\"CREATED\"}";
    }

    @Test
    @DisplayName("Should publish event successfully")
    void publishPaymentCreatedEvent_shouldPublishSuccessfully() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenReturn(jsonEvent);

        publisher.publishPaymentCreatedEvent(event);

        verify(objectMapper).writeValueAsString(event);
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.MAIN_EXCHANGE_NAME),
                eq(RabbitMQConfig.MAIN_QUEUE_NAME),
                eq(jsonEvent)
        );
    }

    @Test
    @DisplayName("Should throw exception when RabbitMQ fails")
    void publishPaymentCreatedEvent_shouldThrowException_whenRabbitMQFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenReturn(jsonEvent);
        doThrow(new AmqpException("RabbitMQ connection failed"))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), anyString());

        assertThatThrownBy(() -> publisher.publishPaymentCreatedEvent(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to publish event to RabbitMQ")
                .hasCauseInstanceOf(AmqpException.class);

        verify(objectMapper).writeValueAsString(event);
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.MAIN_EXCHANGE_NAME),
                eq(RabbitMQConfig.MAIN_QUEUE_NAME),
                eq(jsonEvent)
        );
    }

    @Test
    @DisplayName("Should throw exception when JSON serialization fails")
    void publishPaymentCreatedEvent_shouldThrowException_whenJsonSerializationFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event))
                .thenThrow(new JsonProcessingException("Serialization error") {});

        assertThatThrownBy(() -> publisher.publishPaymentCreatedEvent(event))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(JsonProcessingException.class);

        verify(objectMapper).writeValueAsString(event);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
    }
}

