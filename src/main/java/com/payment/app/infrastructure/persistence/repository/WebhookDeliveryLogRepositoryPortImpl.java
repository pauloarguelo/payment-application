package com.payment.app.infrastructure.persistence.repository;

import com.payment.app.application.port.out.WebhookDeliveryLogRepositoryPort;
import com.payment.app.domain.model.WebhookDeliveryLog;
import com.payment.app.infrastructure.persistence.repository.jpa.WebhookDeliveryLogJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class WebhookDeliveryLogRepositoryPortImpl implements WebhookDeliveryLogRepositoryPort {

    private final WebhookDeliveryLogJpaRepository jpaRepository;

    public WebhookDeliveryLogRepositoryPortImpl(WebhookDeliveryLogJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WebhookDeliveryLog findByPaymentIdAndWebhookId(UUID paymentId, UUID webhookId) {
        return null;
    }

    @Override
    public WebhookDeliveryLog saveLogAsPending(UUID paymentId, UUID webhookId) {
        return null;
    }

    @Override
    public WebhookDeliveryLog updateStatusSuccess(UUID paymentId, UUID webhookI, Integer statusCode) {
        return null;
    }

    @Override
    public WebhookDeliveryLog updateStatusFailed(UUID paymentId, UUID webhookId, Integer statusCode) {
        return null;
    }


}
