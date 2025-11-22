package com.payment.app.domain.model;

import com.payment.app.domain.type.WebhookDeliveryLogStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record WebhookDeliveryLog(
        UUID id,
        UUID paymentId,
        UUID webhookId,
        WebhookDeliveryLogStatus status,
        Integer attemptCount,
        Integer responseCode,
        LocalDateTime lastAttemptAt
) {
}
