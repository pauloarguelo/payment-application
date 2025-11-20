package com.payment.app.application.port.out;

import com.payment.app.domain.model.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}
