package com.payment.app.application.dto;

import com.payment.app.domain.type.PaymentStatus;

import java.util.UUID;

public record PaymentCreateEvent(
        UUID paymentId,
        PaymentStatus status
) { }
