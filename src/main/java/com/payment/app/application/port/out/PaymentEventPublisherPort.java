package com.payment.app.application.port.out;

import com.payment.app.application.dto.CreatePaymentEvent;

public interface PaymentEventPublisherPort {
    void publishPaymentCreatedEvent(CreatePaymentEvent event);
}
