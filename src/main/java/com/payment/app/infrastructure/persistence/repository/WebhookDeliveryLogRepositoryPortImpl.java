package com.payment.app.infrastructure.persistence.repository;

import com.payment.app.application.port.out.WebhookDeliveryLogRepositoryPort;
import com.payment.app.domain.model.WebhookDeliveryLog;
import com.payment.app.domain.type.WebhookDeliveryLogStatus;
import com.payment.app.infrastructure.persistence.entity.WebhookDeliveryLogEntity;
import com.payment.app.infrastructure.persistence.mapper.WebhookDeliveryLogMapper;
import com.payment.app.infrastructure.persistence.repository.jpa.WebhookDeliveryLogJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class WebhookDeliveryLogRepositoryPortImpl implements WebhookDeliveryLogRepositoryPort {

    private final WebhookDeliveryLogJpaRepository jpaRepository;
    private final WebhookDeliveryLogMapper mapper;

    public WebhookDeliveryLogRepositoryPortImpl(
            WebhookDeliveryLogJpaRepository jpaRepository,
            WebhookDeliveryLogMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public WebhookDeliveryLog findByPaymentIdAndWebhookId(UUID paymentId, UUID webhookId) {
        Optional<WebhookDeliveryLogEntity> entity = jpaRepository.findByPaymentIdAndWebhookId(
                paymentId.toString(), webhookId.toString()
        );

        return entity.map(mapper::toDomain).orElse(null);
    }

    @Override
    public WebhookDeliveryLog saveLogAsPending(UUID paymentId, UUID webhookId) {
        WebhookDeliveryLogEntity entity = new WebhookDeliveryLogEntity();
        entity.setPaymentId(paymentId.toString());
        entity.setAttemptCount(0);
        entity.setWebhookId(webhookId.toString());
        entity.setStatus(WebhookDeliveryLogStatus.PENDING);

        WebhookDeliveryLogEntity savedEntity = jpaRepository.saveAndFlush(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public WebhookDeliveryLog updateStatusSuccess(UUID paymentId, UUID webhookId, Integer responseCode) {

        WebhookDeliveryLogEntity entity = jpaRepository.findByPaymentIdAndWebhookId(
                paymentId.toString(), webhookId.toString()
        ).orElseThrow(() -> new RuntimeException("WebhookDeliveryLog register not found in database"));

        entity.setStatus(WebhookDeliveryLogStatus.SUCCESS);
        entity.setResponseCode(responseCode);

        WebhookDeliveryLogEntity savedEntity = jpaRepository.saveAndFlush(entity);
        return mapper.toDomain(savedEntity);
    }


    @Override
    public WebhookDeliveryLog updateStatusFailed(UUID paymentId, UUID webhookId, Integer responseCode) {

        WebhookDeliveryLogEntity entity = jpaRepository.findByPaymentIdAndWebhookId(
                paymentId.toString(), webhookId.toString()
        ).orElseThrow(() -> new RuntimeException("WebhookDeliveryLog register not found in database"));

        entity.setStatus(WebhookDeliveryLogStatus.FAILED);
        entity.setResponseCode(responseCode);
        entity.setLastAttemptAt(java.time.LocalDateTime.now());

        WebhookDeliveryLogEntity savedEntity = jpaRepository.saveAndFlush(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public WebhookDeliveryLog incrementAttemptCount(UUID paymentId, UUID webhookId) {
        WebhookDeliveryLogEntity entity = jpaRepository.findByPaymentIdAndWebhookId(
                paymentId.toString(), webhookId.toString()
        ).orElseThrow(() -> new RuntimeException("WebhookDeliveryLog register not found in database"));

        entity.setAttemptCount(entity.getAttemptCount() + 1);
        entity.setLastAttemptAt(java.time.LocalDateTime.now());

        WebhookDeliveryLogEntity savedEntity = jpaRepository.saveAndFlush(entity);
        return mapper.toDomain(savedEntity);
    }


}
