package com.payment.app.application.port.in;

import com.payment.app.application.dto.PaymentWebhookProcessEvent;

public interface ProcessWebhookDeliveryUseCase {
    void processDelivery(PaymentWebhookProcessEvent event);
}
