package com.payment.app.application.dto;

import com.payment.app.domain.type.PaymentEventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to create a new webhook")
public record WebhookRequest(

        @Schema(description = "Endpoint URL that will receive webhook notifications",
                example = "https://api.example.com/webhooks/payment")
        @NotBlank(message = "Endpoint URL is required")
        @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
        String endpointUrl,

        @Schema(description = "Event type that the webhook should listen to",
                example = "PAYMENT_CREATED",
                allowableValues = {"PAYMENT_CREATED", "PAYMENT_FAILED", "PAYMENT_REFUNDED"})
        @NotNull(message = "Event type is required")
        PaymentEventType eventType
) {
}

