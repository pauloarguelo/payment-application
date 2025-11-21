package com.payment.app.application.port.out;

import com.payment.app.application.dto.WebhookClientResponse;

public interface WebhookClientPort {
    WebhookClientResponse postEvent(String url, String payloadJson);
}
