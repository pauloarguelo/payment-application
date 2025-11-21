package com.payment.app.infrastructure.persistence.repository.jpa;

import com.payment.app.infrastructure.persistence.entity.WebhookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WebhookJpaRepository extends JpaRepository<WebhookEntity, UUID> {
    List<WebhookEntity> findByStatus(String status);
}
