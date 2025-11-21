package com.payment.app.application.port.out;

import com.payment.app.domain.model.WebhookDeliveryLog;

import java.util.UUID;

public interface WebhookDeliveryLogRepositoryPort {
    WebhookDeliveryLog findByPaymentIdAndWebhookId(UUID paymentId, UUID webhookId);

    WebhookDeliveryLog saveLogAsPending(UUID paymentId, UUID webhookId);

    WebhookDeliveryLog updateStatusSuccess(UUID paymentId, UUID webhookId, Integer responseCode);

    WebhookDeliveryLog updateStatusFailed(UUID paymentId, UUID webhookId, Integer responseCode);
}
