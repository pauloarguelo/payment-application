package com.payment.app.application.port.in;

import com.payment.app.application.dto.PaymentCreateEvent;

public interface WebhookDeliveryUseCase {
    void sendPaymentAndWebhooksToProcess(PaymentCreateEvent event);
}
