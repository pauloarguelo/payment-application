package com.payment.app.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record WebhookDeliveryLog(
        UUID id,
        UUID paymentId,
        UUID webhookId,
        String status,
        Integer attemptCount,
        Integer responseCode,
        LocalDateTime lastAttemptAt
) {
}
