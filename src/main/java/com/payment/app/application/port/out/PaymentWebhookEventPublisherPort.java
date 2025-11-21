package com.payment.app.application.port.out;

import com.payment.app.application.dto.PaymentWebhookProcessEvent;

public interface PaymentWebhookEventPublisherPort {
    void publishPaymentWebhookProcessEvent(PaymentWebhookProcessEvent event);
}
