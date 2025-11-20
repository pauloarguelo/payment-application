package com.payment.app.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PaymentRequest(
        @Schema(description = "Customer's first name", example = "John")
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(description = "Customer's last name", example = "Doe")
        @NotBlank(message = "Last name is required")
        String lastName,

        @Schema(description = "Billing Zip Code", example = "2000")
        @NotBlank(message = "Zip code is required")
        String zipCode,

        @Schema(description = "Credit Card Number (16 digits)", example = "1234567812345678")
        @NotBlank(message = "Card number is required")
        @Size(min = 16, max = 16, message = "Card number must be exactly 16 digits")
        @Pattern(regexp = "\\d+", message = "Card number must contain only digits")
        String cardNumber
) { }
