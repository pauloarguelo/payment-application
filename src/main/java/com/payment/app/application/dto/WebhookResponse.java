package com.payment.app.application.dto;

import com.payment.app.domain.type.PaymentEventType;
import com.payment.app.domain.type.WebhookStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response with created webhook data")
public record WebhookResponse(

        @Schema(description = "Unique webhook ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Endpoint URL", example = "https://api.example.com/webhooks/payment")
        String endpointUrl,

        @Schema(description = "Event type", example = "PAYMENT_CREATED")
        PaymentEventType eventType,

        @Schema(description = "Webhook status", example = "ACTIVE")
        WebhookStatus status,

        @Schema(description = "Creation date", example = "2025-11-21T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Last update date", example = "2025-11-21T10:30:00")
        LocalDateTime updatedAt
) {
}

