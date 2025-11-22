package com.payment.app.application.useCase;

import com.payment.app.application.dto.CreatePaymentEvent;
import com.payment.app.application.dto.PaymentWebhookProcessEvent;
import com.payment.app.application.port.out.PaymentWebhookEventPublisherPort;
import com.payment.app.application.port.out.WebhookRepositoryPort;
import com.payment.app.domain.model.Webhook;
import com.payment.app.domain.type.PaymentEventType;
import com.payment.app.domain.type.PaymentStatus;
import com.payment.app.domain.type.WebhookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebhookDeliveryUseCaseImpl Tests")
class WebhookDeliveryUseCaseImplTest {

    @Mock
    private WebhookRepositoryPort webhookRepositoryPort;

    @Mock
    private PaymentWebhookEventPublisherPort publisherPort;

    @InjectMocks
    private WebhookDeliveryUseCaseImpl useCase;

    private CreatePaymentEvent createPaymentEvent;
    private Webhook webhook1;
    private Webhook webhook2;
    private UUID paymentId;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        UUID webhookId1 = UUID.randomUUID();
        UUID webhookId2 = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        createPaymentEvent = new CreatePaymentEvent(
                paymentId,
                PaymentStatus.CREATED
        );

        webhook1 = new Webhook(
                webhookId1,
                "https://example.com/webhook1",
                "secret-key-1",
                PaymentEventType.PAYMENT_CREATED,
                WebhookStatus.ACTIVE,
                now,
                now
        );

        webhook2 = new Webhook(
                webhookId2,
                "https://example.com/webhook2",
                "secret-key-2",
                PaymentEventType.PAYMENT_CREATED,
                WebhookStatus.ACTIVE,
                now,
                now
        );
    }

    @Test
    @DisplayName("Should publish events for all active webhooks")
    void sendPaymentAndWebhooksToProcess_shouldPublishEventsForAllActiveWebhooks() {
        List<Webhook> activeWebhooks = List.of(webhook1, webhook2);
        when(webhookRepositoryPort.findByStatus("ACTIVE")).thenReturn(activeWebhooks);

        useCase.sendPaymentAndWebhooksToProcess(createPaymentEvent);

        verify(webhookRepositoryPort).findByStatus("ACTIVE");
        verify(publisherPort, times(2)).publishPaymentWebhookProcessEvent(any(PaymentWebhookProcessEvent.class));
    }

    @Test
    @DisplayName("Should not publish events when no active webhooks found")
    void sendPaymentAndWebhooksToProcess_shouldNotPublishEvents_whenNoActiveWebhooks() {
        when(webhookRepositoryPort.findByStatus("ACTIVE")).thenReturn(List.of());

        useCase.sendPaymentAndWebhooksToProcess(createPaymentEvent);

        verify(webhookRepositoryPort).findByStatus("ACTIVE");
        verify(publisherPort, never()).publishPaymentWebhookProcessEvent(any());
    }

    @Test
    @DisplayName("Should create correct PaymentWebhookProcessEvent for each webhook")
    void sendPaymentAndWebhooksToProcess_shouldCreateCorrectEvent() {
        List<Webhook> activeWebhooks = List.of(webhook1);
        when(webhookRepositoryPort.findByStatus("ACTIVE")).thenReturn(activeWebhooks);

        useCase.sendPaymentAndWebhooksToProcess(createPaymentEvent);

        verify(publisherPort).publishPaymentWebhookProcessEvent(
                argThat(event -> event.paymentId().equals(paymentId) && event.webhookId().equals(webhook1.id()))
        );
    }

    @Test
    @DisplayName("Should publish event for single active webhook")
    void sendPaymentAndWebhooksToProcess_shouldPublishEventForSingleWebhook() {
        List<Webhook> activeWebhooks = List.of(webhook1);
        when(webhookRepositoryPort.findByStatus("ACTIVE")).thenReturn(activeWebhooks);

        useCase.sendPaymentAndWebhooksToProcess(createPaymentEvent);

        verify(webhookRepositoryPort).findByStatus("ACTIVE");
        verify(publisherPort, times(1)).publishPaymentWebhookProcessEvent(any(PaymentWebhookProcessEvent.class));
    }
}

