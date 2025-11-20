package com.payment.app.application.port.in;

import com.payment.app.application.dto.PaymentRequest;
import com.payment.app.application.dto.PaymentResponse;

public interface CreatePaymentUseCase {
    PaymentResponse createPayment(PaymentRequest payment, String idempotencyKey);
}
