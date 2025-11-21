package com.payment.app.application.port.in;

import com.payment.app.application.dto.CreatePaymentEvent;

public interface WebhookDeliveryUseCase {
    void sendPaymentAndWebhooksToProcess(CreatePaymentEvent event);
}
