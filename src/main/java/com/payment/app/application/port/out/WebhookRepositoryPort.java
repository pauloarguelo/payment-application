package com.payment.app.application.port.out;

import com.payment.app.domain.model.Webhook;

import java.util.List;

public interface WebhookRepositoryPort {
    Webhook save(Webhook webhook);
    List<Webhook> findByStatus(String status);
}
