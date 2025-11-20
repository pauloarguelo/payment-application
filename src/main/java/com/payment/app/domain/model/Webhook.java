package com.payment.app.domain.model;

import com.payment.app.domain.type.PaymentEventType;

public record Webhook(
    String url,
    PaymentEventType eventType
) { }
