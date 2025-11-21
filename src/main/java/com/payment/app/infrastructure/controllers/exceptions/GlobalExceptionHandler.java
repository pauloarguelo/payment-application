package com.payment.app.infrastructure.controllers.exceptions;

import com.payment.app.domain.exceptions.IdempotencyViolationException;
import com.payment.app.infrastructure.controllers.dto.ErrorResponse;
import com.payment.app.infrastructure.controllers.dto.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidationError(MethodArgumentNotValidException ex) {
        List<ValidationErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationErrorResponse.FieldError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        return new ValidationErrorResponse(
                "VALIDATION_ERROR",
                "One or more fields are invalid",
                HttpStatus.BAD_REQUEST.value(),
                fieldErrors
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericError(RuntimeException ex) {
        return new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }

    @ExceptionHandler(IdempotencyViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleGenericError(IdempotencyViolationException ex) {
        return new ErrorResponse(
                "IDEMPOTENCY_ERROR",
                ex.getMessage(),
                HttpStatus.CONFLICT.value()
        );
    }

}
