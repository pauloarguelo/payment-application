package com.payment.app.domain.model;

import com.payment.app.domain.type.PaymentEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public record Webhook(
    UUID id,
    String endpointUrl,
    String secretKey,
    PaymentEventType eventType,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) { }
