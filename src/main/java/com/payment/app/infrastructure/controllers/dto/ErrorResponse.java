package com.payment.app.infrastructure.controllers.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String error,
        String description,
        Integer statusCode,
        LocalDateTime timestamp
) {
    public ErrorResponse(String error, String description, Integer statusCode) {
        this(error, description, statusCode, LocalDateTime.now());
    }
}