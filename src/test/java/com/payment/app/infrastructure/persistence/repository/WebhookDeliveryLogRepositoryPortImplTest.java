package com.payment.app.infrastructure.persistence.repository;

import com.payment.app.domain.model.WebhookDeliveryLog;
import com.payment.app.domain.type.WebhookDeliveryLogStatus;
import com.payment.app.infrastructure.persistence.entity.WebhookDeliveryLogEntity;
import com.payment.app.infrastructure.persistence.mapper.WebhookDeliveryLogMapper;
import com.payment.app.infrastructure.persistence.repository.jpa.WebhookDeliveryLogJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebhookDeliveryLogRepositoryPortImpl Tests")
class WebhookDeliveryLogRepositoryPortImplTest {

    @Mock
    private WebhookDeliveryLogJpaRepository jpaRepository;

    @Mock
    private WebhookDeliveryLogMapper mapper;

    @InjectMocks
    private WebhookDeliveryLogRepositoryPortImpl repository;

    private UUID paymentId;
    private UUID webhookId;
    private UUID deliveryLogId;
    private WebhookDeliveryLog deliveryLog;
    private WebhookDeliveryLogEntity deliveryLogEntity;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        webhookId = UUID.randomUUID();
        deliveryLogId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        deliveryLog = new WebhookDeliveryLog(
                deliveryLogId,
                paymentId,
                webhookId,
                WebhookDeliveryLogStatus.PENDING,
                0,
                null,
                null
        );

        deliveryLogEntity = new WebhookDeliveryLogEntity();
        deliveryLogEntity.setId(deliveryLogId);
        deliveryLogEntity.setPaymentId(paymentId.toString());
        deliveryLogEntity.setWebhookId(webhookId.toString());
        deliveryLogEntity.setStatus(WebhookDeliveryLogStatus.PENDING);
        deliveryLogEntity.setAttemptCount(0);
        deliveryLogEntity.setResponseCode(null);
        deliveryLogEntity.setLastAttemptAt(null);
    }

    @Test
    @DisplayName("Should find delivery log by payment and webhook id when exists")
    void findByPaymentIdAndWebhookId_shouldReturnLog_whenExists() {
        when(jpaRepository.findByPaymentIdAndWebhookId(paymentId.toString(), webhookId.toString()))
                .thenReturn(Optional.of(deliveryLogEntity));
        when(mapper.toDomain(deliveryLogEntity)).thenReturn(deliveryLog);

        WebhookDeliveryLog result = repository.findByPaymentIdAndWebhookId(paymentId, webhookId);

        assertThat(result).isNotNull();
        assertThat(result.paymentId()).isEqualTo(paymentId);
        assertThat(result.webhookId()).isEqualTo(webhookId);
        verify(jpaRepository).findByPaymentIdAndWebhookId(paymentId.toString(), webhookId.toString());
        verify(mapper).toDomain(deliveryLogEntity);
    }

    @Test
    @DisplayName("Should return null when delivery log not found")
    void findByPaymentIdAndWebhookId_shouldReturnNull_whenNotExists() {
        when(jpaRepository.findByPaymentIdAndWebhookId(paymentId.toString(), webhookId.toString()))
                .thenReturn(Optional.empty());

        WebhookDeliveryLog result = repository.findByPaymentIdAndWebhookId(paymentId, webhookId);

        assertThat(result).isNull();
        verify(jpaRepository).findByPaymentIdAndWebhookId(paymentId.toString(), webhookId.toString());
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should save log as pending successfully")
    void saveLogAsPending_shouldSaveSuccessfully() {
        when(jpaRepository.saveAndFlush(any(WebhookDeliveryLogEntity.class))).thenReturn(deliveryLogEntity);
        when(mapper.toDomain(deliveryLogEntity)).thenReturn(deliveryLog);

        WebhookDeliveryLog result = repository.saveLogAsPending(paymentId, webhookId);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(WebhookDeliveryLogStatus.PENDING);
        assertThat(result.attemptCount()).isEqualTo(0);
        verify(jpaRepository).saveAndFlush(any(WebhookDeliveryLogEntity.class));
        verify(mapper).toDomain(deliveryLogEntity);
    }

    @Test
    @DisplayName("Should update status to success")
    void updateStatusSuccess_shouldUpdateSuccessfully() {
        Integer responseCode = 200;
        WebhookDeliveryLog successLog = new WebhookDeliveryLog(
                deliveryLogId,
                paymentId,
                webhookId,
                WebhookDeliveryLogStatus.SUCCESS,
                1,
                responseCode,
                LocalDateTime.now()
        );

        when(jpaRepository.findByPaymentIdAndWebhookId(paymentId.toString(), webhookId.toString()))
                .thenReturn(Optional.of(deliveryLogEntity));
        when(jpaRepository.saveAndFlush(any(WebhookDeliveryLogEntity.class))).thenReturn(deliveryLogEntity);
        when(mapper.toDomain(any(WebhookDeliveryLogEntity.class))).thenReturn(successLog);

        WebhookDeliveryLog result = repository.updateStatusSuccess(paymentId, webhookId, responseCode);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(WebhookDeliveryLogStatus.SUCCESS);
        assertThat(result.responseCode()).isEqualTo(responseCode);
        verify(jpaRepository).findByPaymentIdAndWebhookId(paymentId.toString(), webhookId.toString());
        verify(jpaRepository).saveAndFlush(any(WebhookDeliveryLogEntity.class));
    }

    @Test
    @DisplayName("Should update status to failed")
    void updateStatusFailed_shouldUpdateSuccessfully() {
        Integer responseCode = 400;
        WebhookDeliveryLog failedLog = new WebhookDeliveryLog(
                deliveryLogId,
                paymentId,
                webhookId,
                WebhookDeliveryLogStatus.FAILED,
                1,
                responseCode,
                LocalDateTime.now()
        );

        when(jpaRepository.findByPaymentIdAndWebhookId(paymentId.toString(), webhookId.toString()))
                .thenReturn(Optional.of(deliveryLogEntity));
        when(jpaRepository.saveAndFlush(any(WebhookDeliveryLogEntity.class))).thenReturn(deliveryLogEntity);
        when(mapper.toDomain(any(WebhookDeliveryLogEntity.class))).thenReturn(failedLog);

        WebhookDeliveryLog result = repository.updateStatusFailed(paymentId, webhookId, responseCode);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(WebhookDeliveryLogStatus.FAILED);
        assertThat(result.responseCode()).isEqualTo(responseCode);
        verify(jpaRepository).findByPaymentIdAndWebhookId(paymentId.toString(), webhookId.toString());
        verify(jpaRepository).saveAndFlush(any(WebhookDeliveryLogEntity.class));
    }

    @Test
    @DisplayName("Should increment attempt count")
    void incrementAttemptCount_shouldIncrementSuccessfully() {
        deliveryLogEntity.setAttemptCount(1);
        WebhookDeliveryLog updatedLog = new WebhookDeliveryLog(
                deliveryLogId,
                paymentId,
                webhookId,
                WebhookDeliveryLogStatus.PENDING,
                2,
                null,
                LocalDateTime.now()
        );

        when(jpaRepository.findByPaymentIdAndWebhookId(paymentId.toString(), webhookId.toString()))
                .thenReturn(Optional.of(deliveryLogEntity));
        when(jpaRepository.saveAndFlush(any(WebhookDeliveryLogEntity.class))).thenReturn(deliveryLogEntity);
        when(mapper.toDomain(any(WebhookDeliveryLogEntity.class))).thenReturn(updatedLog);

        WebhookDeliveryLog result = repository.incrementAttemptCount(paymentId, webhookId);

        assertThat(result).isNotNull();
        assertThat(result.attemptCount()).isEqualTo(2);
        verify(jpaRepository).findByPaymentIdAndWebhookId(paymentId.toString(), webhookId.toString());
        verify(jpaRepository).saveAndFlush(any(WebhookDeliveryLogEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when log not found for update")
    void updateStatusSuccess_shouldThrowException_whenNotFound() {
        when(jpaRepository.findByPaymentIdAndWebhookId(paymentId.toString(), webhookId.toString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> repository.updateStatusSuccess(paymentId, webhookId, 200))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("WebhookDeliveryLog register not found in database");

        verify(jpaRepository).findByPaymentIdAndWebhookId(paymentId.toString(), webhookId.toString());
        verify(jpaRepository, never()).saveAndFlush(any());
    }
}

