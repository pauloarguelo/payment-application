package com.payment.app.application.dto;


public record WebhookClientResponse(
        int statusCode,
        String responseBody
) {
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }

    public boolean isPermanentError() {
        return statusCode >= 400 && statusCode < 500;
    }
}