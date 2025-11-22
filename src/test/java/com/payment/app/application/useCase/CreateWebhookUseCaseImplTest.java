package com.payment.app.application.useCase;

import com.payment.app.application.dto.WebhookRequest;
import com.payment.app.application.dto.WebhookResponse;
import com.payment.app.application.mapper.WebhookApplicationMapper;
import com.payment.app.application.port.out.WebhookRepositoryPort;
import com.payment.app.domain.model.Webhook;
import com.payment.app.domain.type.PaymentEventType;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateWebhookUseCaseImpl Tests")
class CreateWebhookUseCaseImplTest {

    @Mock
    private WebhookRepositoryPort webhookRepository;

    @Mock
    private WebhookApplicationMapper webhookMapper;

    @InjectMocks
    private CreateWebhookUseCaseImpl useCase;

    private WebhookRequest webhookRequest;
    private Webhook webhook;
    private Webhook savedWebhook;
    private WebhookResponse webhookResponse;
    private UUID webhookId;

    @BeforeEach
    void setUp() {
        webhookId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        webhookRequest = new WebhookRequest(
                "https://example.com/webhook",
                PaymentEventType.PAYMENT_CREATED
        );

        webhook = new Webhook(
                null,
                "https://example.com/webhook",
                "secret-key-123",
                PaymentEventType.PAYMENT_CREATED,
                WebhookStatus.ACTIVE,
                now,
                now
        );

        savedWebhook = new Webhook(
                webhookId,
                "https://example.com/webhook",
                "secret-key-123",
                PaymentEventType.PAYMENT_CREATED,
                WebhookStatus.ACTIVE,
                now,
                now
        );

        webhookResponse = new WebhookResponse(
                webhookId,
                "https://example.com/webhook",
                PaymentEventType.PAYMENT_CREATED,
                WebhookStatus.ACTIVE,
                now,
                now
        );
    }

    @Test
    @DisplayName("Should create webhook successfully")
    void createWebhook_shouldCreateSuccessfully() {
        when(webhookMapper.toDomain(eq(webhookRequest), eq(WebhookStatus.ACTIVE), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(webhook);
        when(webhookRepository.save(webhook)).thenReturn(savedWebhook);
        when(webhookMapper.toResponse(savedWebhook)).thenReturn(webhookResponse);

        WebhookResponse result = useCase.createWebhook(webhookRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(webhookId);
        assertThat(result.endpointUrl()).isEqualTo("https://example.com/webhook");
        assertThat(result.eventType()).isEqualTo(PaymentEventType.PAYMENT_CREATED);
        assertThat(result.status()).isEqualTo(WebhookStatus.ACTIVE);
        verify(webhookMapper).toDomain(eq(webhookRequest), eq(WebhookStatus.ACTIVE), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(webhookRepository).save(webhook);
        verify(webhookMapper).toResponse(savedWebhook);
    }

    @Test
    @DisplayName("Should set webhook status as ACTIVE")
    void createWebhook_shouldSetStatusAsActive() {
        when(webhookMapper.toDomain(eq(webhookRequest), eq(WebhookStatus.ACTIVE), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(webhook);
        when(webhookRepository.save(webhook)).thenReturn(savedWebhook);
        when(webhookMapper.toResponse(savedWebhook)).thenReturn(webhookResponse);

        useCase.createWebhook(webhookRequest);

        verify(webhookMapper).toDomain(eq(webhookRequest), eq(WebhookStatus.ACTIVE), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should save webhook to repository")
    void createWebhook_shouldSaveToRepository() {
        when(webhookMapper.toDomain(eq(webhookRequest), eq(WebhookStatus.ACTIVE), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(webhook);
        when(webhookRepository.save(webhook)).thenReturn(savedWebhook);
        when(webhookMapper.toResponse(savedWebhook)).thenReturn(webhookResponse);

        useCase.createWebhook(webhookRequest);

        verify(webhookRepository).save(webhook);
    }
}

