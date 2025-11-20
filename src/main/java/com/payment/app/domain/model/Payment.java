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
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        private UUID paymentId;
        private String firstName;
        private String lastName;
        private String zipCode;
        private String encryptedCardNumber;
        private String idempotencyKey;
        private String status;
        private LocalDateTime createdAt;

        public Builder() {
        }

        public Builder(Payment payment) {
            this.paymentId = payment.paymentId();
            this.firstName = payment.firstName();
            this.lastName = payment.lastName();
            this.zipCode = payment.zipCode();
            this.encryptedCardNumber = payment.encryptedCardNumber();
            this.idempotencyKey = payment.idempotencyKey();
            this.status = payment.status();
            this.createdAt = payment.createdAt();
        }

        public Builder paymentId(UUID paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder zipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Builder encryptedCardNumber(String encryptedCardNumber) {
            this.encryptedCardNumber = encryptedCardNumber;
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Payment build() {
            return new Payment(
                    paymentId,
                    firstName,
                    lastName,
                    zipCode,
                    encryptedCardNumber,
                    idempotencyKey,
                    status,
                    createdAt
            );
        }
    }
}
