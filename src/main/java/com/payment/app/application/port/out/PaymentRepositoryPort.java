package com.payment.app.application.port.out;

import com.payment.app.domain.model.Payment;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);
}
