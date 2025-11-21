package com.payment.app.infrastructure.persistence.repository.jpa;

import com.payment.app.infrastructure.persistence.entity.WebhookDeliveryLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WebhookDeliveryLogJpaRepository extends JpaRepository<WebhookDeliveryLogEntity, UUID> {
}
