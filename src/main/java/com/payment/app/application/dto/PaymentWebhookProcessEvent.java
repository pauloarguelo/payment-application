package com.payment.app.application.dto;

import java.util.UUID;

public record PaymentWebhookProcessEvent(
        UUID paymentId,
        UUID webhookId
) { }
