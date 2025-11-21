package com.payment.app.domain.exceptions;

public class IdempotencyViolationException extends RuntimeException{
    public IdempotencyViolationException(String message) {
        super(message);
    }

    public IdempotencyViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
