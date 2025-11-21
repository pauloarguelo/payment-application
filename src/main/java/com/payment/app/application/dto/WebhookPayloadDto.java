package com.payment.app.application.dto;

import java.util.UUID;

public record WebhookPayloadDto(
        UUID paymentId,
        String event
){}