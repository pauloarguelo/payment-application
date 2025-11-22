package com.payment.app.domain.model;

import com.payment.app.domain.type.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record Payment(
        UUID paymentId,
        String firstName,
        String lastName,
        String zipCode,
        String encryptedCardNumber,
        String idempotencyKey,
        PaymentStatus status,
        LocalDateTime createdAt
) {

    public Payment toDomain(String idempotencyKey, PaymentStatus status, String encryptedCardNumber) {
        return new Payment(
                this.paymentId,
                this.firstName,
                this.lastName,
                this.zipCode,
                encryptedCardNumber,
                idempotencyKey,
                status,
                this.createdAt
        );
    }

}
