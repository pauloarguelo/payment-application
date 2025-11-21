package com.payment.app.application.port.out;

public interface EventPublisherPort {
    boolean publishPaymentCreatedEvent(String paymentDetailsJson);
}
