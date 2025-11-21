package com.payment.app.application.port.out;

import com.payment.app.application.dto.WebhookResponse;

public interface WebhookClientPort {
    WebhookResponse postEvent(String url, String payloadJson);
}
