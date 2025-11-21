package com.payment.app.infrastructure.controllers.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        String error,
        String description,
        Integer statusCode,
        List<FieldError> fieldErrors,
        LocalDateTime timestamp
) {
    public ValidationErrorResponse(String error, String description, Integer statusCode, List<FieldError> fieldErrors) {
        this(error, description, statusCode, fieldErrors, LocalDateTime.now());
    }

    public record FieldError(
            String field,
            String message
    ) {}
}

