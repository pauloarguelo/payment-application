package com.payment.app.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Payment(
        UUID paymentId,
        String firstName,
        String lastName,
        String zipCode,
        String encryptedCardNumber,
        String idempotencyKey,
        String status,
        LocalDateTime createdAt
) {

    public Payment withIdempotencyKey(String idempotencyKey) {
        return new Payment(
                this.paymentId,
                this.firstName,
                this.lastName,
                this.zipCode,
                this.encryptedCardNumber,
                idempotencyKey,
                this.status,
                this.createdAt
        );
    }

    public Payment withEncryptedCardNumber(String encryptedCardNumber) {
        return new Payment(
                this.paymentId,
                this.firstName,
                this.lastName,
                this.zipCode,
                encryptedCardNumber,
                this.idempotencyKey,
                this.status,
                this.createdAt
        );
    }

    public Payment withStatus(String status) {
        return new Payment(
                this.paymentId,
                this.firstName,
                this.lastName,
                this.zipCode,
                this.encryptedCardNumber,
                this.idempotencyKey,
                status,
                this.createdAt
        );
    }


}
