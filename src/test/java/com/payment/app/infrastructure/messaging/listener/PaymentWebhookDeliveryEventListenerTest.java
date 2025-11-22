package com.payment.app.infrastructure.messaging.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.app.application.dto.PaymentWebhookProcessEvent;
import com.payment.app.application.port.in.ProcessWebhookDeliveryUseCase;
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
@DisplayName("PaymentWebhookDeliveryEventListener Tests")
class PaymentWebhookDeliveryEventListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProcessWebhookDeliveryUseCase useCase;

    @InjectMocks
    private PaymentWebhookDeliveryEventListener listener;

    private UUID paymentId;
    private UUID webhookId;
    private String eventPayload;
    private PaymentWebhookProcessEvent event;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        webhookId = UUID.randomUUID();
        eventPayload = "{\"paymentId\":\"" + paymentId + "\",\"webhookId\":\"" + webhookId + "\"}";
        event = new PaymentWebhookProcessEvent(paymentId, webhookId);
    }

    @Test
    @DisplayName("Should process event successfully")
    void processEvent_shouldProcessSuccessfully() throws Exception {
        when(objectMapper.readValue(eventPayload, PaymentWebhookProcessEvent.class)).thenReturn(event);

        listener.processEvent(eventPayload);

        verify(objectMapper).readValue(eventPayload, PaymentWebhookProcessEvent.class);
        verify(useCase).processDelivery(event);
    }

    @Test
    @DisplayName("Should throw exception when JSON deserialization fails")
    void processEvent_shouldThrowException_whenDeserializationFails() throws Exception {
        when(objectMapper.readValue(eventPayload, PaymentWebhookProcessEvent.class))
                .thenThrow(new RuntimeException("Invalid JSON"));

        assertThatThrownBy(() -> listener.processEvent(eventPayload))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(RuntimeException.class);

        verify(objectMapper).readValue(eventPayload, PaymentWebhookProcessEvent.class);
        verify(useCase, never()).processDelivery(any());
    }

    @Test
    @DisplayName("Should throw exception when use case processing fails")
    void processEvent_shouldThrowException_whenUseCaseFails() throws Exception {
        when(objectMapper.readValue(eventPayload, PaymentWebhookProcessEvent.class)).thenReturn(event);
        doThrow(new RuntimeException("Processing failed")).when(useCase).processDelivery(event);

        assertThatThrownBy(() -> listener.processEvent(eventPayload))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(RuntimeException.class);

        verify(objectMapper).readValue(eventPayload, PaymentWebhookProcessEvent.class);
        verify(useCase).processDelivery(event);
    }
}

