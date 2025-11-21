package com.payment.app.domain.model;

import com.payment.app.domain.type.PaymentEventType;
import com.payment.app.domain.type.WebhookStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record Webhook(
    UUID id,
    String endpointUrl,
    String secretKey,
    PaymentEventType eventType,
    WebhookStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) { }
