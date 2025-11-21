package com.payment.app.infrastructure.persistence.repository.jpa;

import com.payment.app.infrastructure.persistence.entity.WebhookDeliveryLogEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WebhookDeliveryLogJpaRepository extends JpaRepository<WebhookDeliveryLogEntity, UUID> {
    Optional<WebhookDeliveryLogEntity> findByPaymentIdAndWebhookId(String paymentId , String webhookId);
}
