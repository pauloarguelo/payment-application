package com.payment.app.application.port.in;

import com.payment.app.application.dto.WebhookRequest;
import com.payment.app.application.dto.WebhookResponse;

public interface CreateWebhookUseCase {
    WebhookResponse createWebhook(WebhookRequest webhook);
}
