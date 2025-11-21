package com.payment.app.application.port.out;

import com.payment.app.domain.model.Webhook;

import java.util.List;
import java.util.UUID;

public interface WebhookRepositoryPort {
    Webhook findById(UUID id);
    Webhook save(Webhook webhook);
    List<Webhook> findByStatus(String status);
}
