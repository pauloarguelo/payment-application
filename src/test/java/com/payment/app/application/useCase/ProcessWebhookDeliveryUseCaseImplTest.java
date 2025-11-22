package com.payment.app.application.useCase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.app.application.dto.PaymentWebhookProcessEvent;
import com.payment.app.application.dto.WebhookClientResponse;
import com.payment.app.application.port.out.WebhookClientPort;
import com.payment.app.application.port.out.WebhookDeliveryLogRepositoryPort;
import com.payment.app.application.port.out.WebhookRepositoryPort;
import com.payment.app.domain.model.Webhook;
import com.payment.app.domain.model.WebhookDeliveryLog;
import com.payment.app.domain.type.PaymentEventType;
import com.payment.app.domain.type.WebhookDeliveryLogStatus;
import com.payment.app.domain.type.WebhookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcessWebhookDeliveryUseCaseImpl Tests")
class ProcessWebhookDeliveryUseCaseImplTest {

    @Mock
    private WebhookRepositoryPort webhookRepository;

    @Mock
    private WebhookDeliveryLogRepositoryPort webhookDeliveryLogRepository;

    @Mock
    private WebhookClientPort webhookClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProcessWebhookDeliveryUseCaseImpl useCase;

    private UUID paymentId;
    private UUID webhookId;
    private UUID deliveryLogId;
    private PaymentWebhookProcessEvent event;
    private Webhook webhook;
    private WebhookDeliveryLog deliveryLog;
    private String payloadJson;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        webhookId = UUID.randomUUID();
        deliveryLogId = UUID.randomUUID();
        event = new PaymentWebhookProcessEvent(paymentId, webhookId);

        LocalDateTime now = LocalDateTime.now();
        webhook = new Webhook(
                webhookId,
                "https://example.com/webhook",
                "secret-key",
                PaymentEventType.PAYMENT_CREATED,
                WebhookStatus.ACTIVE,
                now,
                now
        );

        deliveryLog = new WebhookDeliveryLog(
                deliveryLogId,
                paymentId,
                webhookId,
                WebhookDeliveryLogStatus.PENDING,
                0,
                null,
                null
        );

        payloadJson = "{\"paymentId\":\"" + paymentId + "\",\"event\":\"EVENT DESCRIPTION\"}";
    }

    @Test
    @DisplayName("Should process delivery successfully with 200 response")
    void processDelivery_shouldProcessSuccessfully_when200Response() throws JsonProcessingException {
        WebhookClientResponse response = new WebhookClientResponse(200, "OK");

        when(webhookRepository.findById(webhookId)).thenReturn(webhook);
        when(webhookDeliveryLogRepository.findByPaymentIdAndWebhookId(paymentId, webhookId)).thenReturn(deliveryLog);
        when(objectMapper.writeValueAsString(any())).thenReturn(payloadJson);
        when(webhookClient.postEvent(webhook.endpointUrl(), payloadJson)).thenReturn(response);

        useCase.processDelivery(event);

        verify(webhookRepository).findById(webhookId);
        verify(webhookDeliveryLogRepository).findByPaymentIdAndWebhookId(paymentId, webhookId);
        verify(webhookDeliveryLogRepository).incrementAttemptCount(paymentId, webhookId);
        verify(webhookClient).postEvent(webhook.endpointUrl(), payloadJson);
        verify(webhookDeliveryLogRepository).updateStatusSuccess(paymentId, webhookId, 200);
    }

    @Test
    @DisplayName("Should create delivery log when not exists")
    void processDelivery_shouldCreateDeliveryLog_whenNotExists() throws JsonProcessingException {
        WebhookClientResponse response = new WebhookClientResponse(200, "OK");

        when(webhookRepository.findById(webhookId)).thenReturn(webhook);
        when(webhookDeliveryLogRepository.findByPaymentIdAndWebhookId(paymentId, webhookId)).thenReturn(null);
        when(objectMapper.writeValueAsString(any())).thenReturn(payloadJson);
        when(webhookClient.postEvent(webhook.endpointUrl(), payloadJson)).thenReturn(response);

        useCase.processDelivery(event);

        verify(webhookDeliveryLogRepository).saveLogAsPending(paymentId, webhookId);
        verify(webhookDeliveryLogRepository).incrementAttemptCount(paymentId, webhookId);
        verify(webhookDeliveryLogRepository).updateStatusSuccess(paymentId, webhookId, 200);
    }

    @Test
    @DisplayName("Should skip processing when already delivered successfully")
    void processDelivery_shouldSkip_whenAlreadyDelivered() {
        WebhookDeliveryLog successLog = new WebhookDeliveryLog(
                deliveryLogId,
                paymentId,
                webhookId,
                WebhookDeliveryLogStatus.SUCCESS,
                1,
                200,
                LocalDateTime.now()
        );

        when(webhookRepository.findById(webhookId)).thenReturn(webhook);
        when(webhookDeliveryLogRepository.findByPaymentIdAndWebhookId(paymentId, webhookId)).thenReturn(successLog);

        useCase.processDelivery(event);

        verify(webhookRepository).findById(webhookId);
        verify(webhookDeliveryLogRepository).findByPaymentIdAndWebhookId(paymentId, webhookId);
        verify(webhookDeliveryLogRepository, never()).incrementAttemptCount(any(), any());
        verify(webhookClient, never()).postEvent(any(), any());
    }

    @Test
    @DisplayName("Should mark as failed when permanent error 400")
    void processDelivery_shouldMarkAsFailed_when400Error() throws JsonProcessingException {
        WebhookClientResponse response = new WebhookClientResponse(400, "Bad Request");

        when(webhookRepository.findById(webhookId)).thenReturn(webhook);
        when(webhookDeliveryLogRepository.findByPaymentIdAndWebhookId(paymentId, webhookId)).thenReturn(deliveryLog);
        when(objectMapper.writeValueAsString(any())).thenReturn(payloadJson);
        when(webhookClient.postEvent(webhook.endpointUrl(), payloadJson)).thenReturn(response);

        useCase.processDelivery(event);

        verify(webhookDeliveryLogRepository).updateStatusFailed(paymentId, webhookId, 400);
        verify(webhookDeliveryLogRepository, never()).updateStatusSuccess(any(), any(), anyInt());
    }

    @Test
    @DisplayName("Should throw exception for transient error 500")
    void processDelivery_shouldThrowException_when500Error() throws JsonProcessingException {
        WebhookClientResponse response = new WebhookClientResponse(500, "Internal Server Error");

        when(webhookRepository.findById(webhookId)).thenReturn(webhook);
        when(webhookDeliveryLogRepository.findByPaymentIdAndWebhookId(paymentId, webhookId)).thenReturn(deliveryLog);
        when(objectMapper.writeValueAsString(any())).thenReturn(payloadJson);
        when(webhookClient.postEvent(webhook.endpointUrl(), payloadJson)).thenReturn(response);

        assertThatThrownBy(() -> useCase.processDelivery(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Connection failure")
                .hasCauseInstanceOf(RuntimeException.class);

        verify(webhookDeliveryLogRepository, never()).updateStatusSuccess(any(), any(), anyInt());
        verify(webhookDeliveryLogRepository, never()).updateStatusFailed(any(), any(), anyInt());
    }

    @Test
    @DisplayName("Should throw exception when webhook not found")
    void processDelivery_shouldThrowException_whenWebhookNotFound() {
        when(webhookRepository.findById(webhookId)).thenReturn(null);

        assertThatThrownBy(() -> useCase.processDelivery(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Webhook not found");

        verify(webhookRepository).findById(webhookId);
        verify(webhookClient, never()).postEvent(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when JSON serialization fails")
    void processDelivery_shouldThrowException_whenJsonSerializationFails() throws JsonProcessingException {
        when(webhookRepository.findById(webhookId)).thenReturn(webhook);
        when(webhookDeliveryLogRepository.findByPaymentIdAndWebhookId(paymentId, webhookId)).thenReturn(deliveryLog);
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization error") {});

        assertThatThrownBy(() -> useCase.processDelivery(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Connection failure")
                .hasCauseInstanceOf(RuntimeException.class);

        verify(webhookClient, never()).postEvent(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when webhook client fails")
    void processDelivery_shouldThrowException_whenWebhookClientFails() throws JsonProcessingException {
        when(webhookRepository.findById(webhookId)).thenReturn(webhook);
        when(webhookDeliveryLogRepository.findByPaymentIdAndWebhookId(paymentId, webhookId)).thenReturn(deliveryLog);
        when(objectMapper.writeValueAsString(any())).thenReturn(payloadJson);
        when(webhookClient.postEvent(webhook.endpointUrl(), payloadJson))
                .thenThrow(new RuntimeException("Network failure"));

        assertThatThrownBy(() -> useCase.processDelivery(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Connection failure")
                .hasCauseInstanceOf(RuntimeException.class);

        verify(webhookClient).postEvent(webhook.endpointUrl(), payloadJson);
    }
}

