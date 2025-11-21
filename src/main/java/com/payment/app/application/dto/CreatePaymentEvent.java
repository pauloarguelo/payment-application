package com.payment.app.application.dto;

import com.payment.app.domain.type.PaymentStatus;

import java.util.UUID;

public record CreatePaymentEvent(
        UUID paymentId,
        PaymentStatus status
) { }
