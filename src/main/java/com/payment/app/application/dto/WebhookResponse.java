package com.payment.app.application.dto;

import com.payment.app.domain.type.PaymentEventType;
import com.payment.app.domain.type.WebhookStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response com os dados do webhook criado")
public record WebhookResponse(

        @Schema(description = "ID único do webhook", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "URL do endpoint", example = "https://api.example.com/webhooks/payment")
        String endpointUrl,

        @Schema(description = "Tipo de evento", example = "PAYMENT_CREATED")
        PaymentEventType eventType,

        @Schema(description = "Status do webhook", example = "ACTIVE")
        WebhookStatus status,

        @Schema(description = "Data de criação", example = "2025-11-21T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Data de atualização", example = "2025-11-21T10:30:00")
        LocalDateTime updatedAt
) {
}

