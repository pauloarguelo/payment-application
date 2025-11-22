package com.payment.app.infrastructure.persistence.repository;

import com.payment.app.domain.model.Webhook;
import com.payment.app.domain.type.PaymentEventType;
import com.payment.app.domain.type.WebhookStatus;
import com.payment.app.infrastructure.persistence.entity.WebhookEntity;
import com.payment.app.infrastructure.persistence.mapper.WebhookPersistenceMapper;
import com.payment.app.infrastructure.persistence.repository.jpa.WebhookJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebhookRepositoryPortImpl Tests")
class WebhookRepositoryPortImplTest {

    @Mock
    private WebhookJpaRepository jpaRepository;

    @Mock
    private WebhookPersistenceMapper mapper;

    @InjectMocks
    private WebhookRepositoryPortImpl repository;

    private UUID webhookId;
    private Webhook webhook;
    private WebhookEntity webhookEntity;

    @BeforeEach
    void setUp() {
        webhookId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        webhook = new Webhook(
                webhookId,
                "https://example.com/webhook",
                "secret-key-123",
                PaymentEventType.PAYMENT_CREATED,
                WebhookStatus.ACTIVE,
                now,
                now
        );

        webhookEntity = new WebhookEntity();
        webhookEntity.setId(webhookId);
        webhookEntity.setEndpointUrl("https://example.com/webhook");
        webhookEntity.setSecretKey("secret-key-123");
        webhookEntity.setEventType(PaymentEventType.PAYMENT_CREATED.name());
        webhookEntity.setStatus(WebhookStatus.ACTIVE.name());
        webhookEntity.setCreatedAt(now);
        webhookEntity.setUpdatedAt(now);
    }

    @Test
    @DisplayName("Should return webhook when found by id")
    void findById_shouldReturnWebhook_whenExists() {
        when(jpaRepository.findById(webhookId)).thenReturn(Optional.of(webhookEntity));
        when(mapper.toDomain(webhookEntity)).thenReturn(webhook);

        Webhook result = repository.findById(webhookId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(webhookId);
        verify(jpaRepository).findById(webhookId);
        verify(mapper).toDomain(webhookEntity);
    }

    @Test
    @DisplayName("Should return null when webhook not found by id")
    void findById_shouldReturnNull_whenNotExists() {
        when(jpaRepository.findById(webhookId)).thenReturn(Optional.empty());

        Webhook result = repository.findById(webhookId);

        assertThat(result).isNull();
        verify(jpaRepository).findById(webhookId);
        verify(mapper, never()).toDomain(any(WebhookEntity.class));
    }

    @Test
    @DisplayName("Should save webhook successfully")
    void save_shouldReturnSavedWebhook() {
        when(mapper.toEntity(webhook)).thenReturn(webhookEntity);
        when(jpaRepository.save(webhookEntity)).thenReturn(webhookEntity);
        when(mapper.toDomain(webhookEntity)).thenReturn(webhook);

        Webhook result = repository.save(webhook);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(webhookId);
        verify(mapper).toEntity(webhook);
        verify(jpaRepository).save(webhookEntity);
        verify(mapper).toDomain(webhookEntity);
    }

    @Test
    @DisplayName("Should return list of webhooks by status")
    void findByStatus_shouldReturnWebhookList() {
        String status = WebhookStatus.ACTIVE.name();
        List<WebhookEntity> entities = List.of(webhookEntity);
        List<Webhook> webhooks = List.of(webhook);

        when(jpaRepository.findByStatus(status)).thenReturn(entities);
        when(mapper.toDomain(entities)).thenReturn(webhooks);

        List<Webhook> result = repository.findByStatus(status);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        verify(jpaRepository).findByStatus(status);
        verify(mapper).toDomain(entities);
    }

    @Test
    @DisplayName("Should return empty list when no webhooks found by status")
    void findByStatus_shouldReturnEmptyList_whenNoWebhooksFound() {
        String status = WebhookStatus.INACTIVE.name();
        List<WebhookEntity> emptyEntities = List.of();
        List<Webhook> emptyWebhooks = List.of();

        when(jpaRepository.findByStatus(status)).thenReturn(emptyEntities);
        when(mapper.toDomain(emptyEntities)).thenReturn(emptyWebhooks);

        List<Webhook> result = repository.findByStatus(status);

        assertThat(result).isEmpty();
        verify(jpaRepository).findByStatus(status);
        verify(mapper).toDomain(emptyEntities);
    }
}

