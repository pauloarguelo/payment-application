package com.payment.app.domain.model;

import java.util.UUID;

public record Payment(
        UUID paymentId,
        String firstName,
        String lastName,
        String zipCode,
        String cardNumber
) { }
