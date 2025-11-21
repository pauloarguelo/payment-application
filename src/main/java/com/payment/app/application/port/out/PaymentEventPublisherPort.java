package com.payment.app.application.port.out;

import com.payment.app.application.dto.PaymentCreateEvent;

public interface PaymentEventPublisherPort {
    void publishPaymentCreatedEvent(PaymentCreateEvent event);
}
