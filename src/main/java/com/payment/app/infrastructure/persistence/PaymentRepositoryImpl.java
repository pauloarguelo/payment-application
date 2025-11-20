package com.payment.app.infrastructure.persistence;

import com.payment.app.application.port.out.PaymentRepository;
import com.payment.app.domain.model.Payment;
import org.springframework.stereotype.Service;


@Service
public class PaymentRepositoryImpl implements PaymentRepository {
    @Override
    public Payment save(Payment payment) {
        return payment;
    }
}
