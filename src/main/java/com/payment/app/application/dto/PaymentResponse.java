package com.payment.app.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        String firstName,
        String lastName,
        //PaymentStatus status,
        LocalDateTime createdAt
) {}