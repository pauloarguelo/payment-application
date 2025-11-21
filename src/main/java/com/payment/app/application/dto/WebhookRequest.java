package com.payment.app.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request para criar um novo webhook")
public record WebhookRequest(

        @Schema(description = "URL do endpoint que receberá as notificações do webhook",
                example = "https://api.example.com/webhooks/payment")
        @NotBlank(message = "Endpoint URL é obrigatório")
        @Pattern(regexp = "^https?://.*", message = "URL deve começar com http:// ou https://")
        String endpointUrl,

        @Schema(description = "Tipo de evento que o webhook deve escutar",
                example = "PAYMENT_CREATED",
                allowableValues = {"PAYMENT_CREATED", "PAYMENT_FAILED", "PAYMENT_REFUNDED"})
        @NotNull(message = "Tipo de evento é obrigatório")
        String eventType
) {
}
