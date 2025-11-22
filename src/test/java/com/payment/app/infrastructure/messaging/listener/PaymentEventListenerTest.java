package com.payment.app.infrastructure.messaging.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.app.application.dto.CreatePaymentEvent;
import com.payment.app.application.port.in.WebhookDeliveryUseCase;
import com.payment.app.domain.type.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentEventListener Tests")
class PaymentEventListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebhookDeliveryUseCase webhookDeliveryUseCase;

    @InjectMocks
    private PaymentEventListener listener;

    private String eventPayload;
    private CreatePaymentEvent event;
    private UUID paymentId;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        eventPayload = "{\"paymentId\":\"" + paymentId + "\",\"status\":\"CREATED\"}";
        event = new CreatePaymentEvent(paymentId, PaymentStatus.CREATED);
    }

    @Test
    @DisplayName("Should process event successfully")
    void processEvent_shouldProcessSuccessfully() throws Exception {
        when(objectMapper.readValue(eventPayload, CreatePaymentEvent.class)).thenReturn(event);

        listener.processEvent(eventPayload);

        verify(objectMapper).readValue(eventPayload, CreatePaymentEvent.class);
        verify(webhookDeliveryUseCase).sendPaymentAndWebhooksToProcess(event);
    }

    @Test
    @DisplayName("Should throw exception when JSON deserialization fails")
    void processEvent_shouldThrowException_whenDeserializationFails() throws Exception {
        when(objectMapper.readValue(eventPayload, CreatePaymentEvent.class))
                .thenThrow(new RuntimeException("Invalid JSON"));

        assertThatThrownBy(() -> listener.processEvent(eventPayload))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(RuntimeException.class);

        verify(objectMapper).readValue(eventPayload, CreatePaymentEvent.class);
        verify(webhookDeliveryUseCase, never()).sendPaymentAndWebhooksToProcess(any());
    }

    @Test
    @DisplayName("Should throw exception when use case fails")
    void processEvent_shouldThrowException_whenUseCaseFails() throws Exception {
        when(objectMapper.readValue(eventPayload, CreatePaymentEvent.class)).thenReturn(event);
        doThrow(new RuntimeException("Use case failed")).when(webhookDeliveryUseCase).sendPaymentAndWebhooksToProcess(event);

        assertThatThrownBy(() -> listener.processEvent(eventPayload))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(RuntimeException.class);

        verify(objectMapper).readValue(eventPayload, CreatePaymentEvent.class);
        verify(webhookDeliveryUseCase).sendPaymentAndWebhooksToProcess(event);
    }
}

