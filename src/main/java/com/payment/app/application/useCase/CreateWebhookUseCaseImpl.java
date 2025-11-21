package com.payment.app.application.useCase;

import com.payment.app.application.dto.WebhookRequest;
import com.payment.app.application.dto.WebhookResponse;
import com.payment.app.application.mapper.WebhookApplicationMapper;
import com.payment.app.application.port.in.CreateWebhookUseCase;
import com.payment.app.application.port.out.WebhookRepositoryPort;
import com.payment.app.domain.model.Webhook;
import com.payment.app.domain.type.WebhookStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreateWebhookUseCaseImpl implements CreateWebhookUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateWebhookUseCaseImpl.class);
    private final WebhookRepositoryPort webhookRepository;
    private final WebhookApplicationMapper webhookMapper;

    public CreateWebhookUseCaseImpl(
            WebhookRepositoryPort webhookRepository,
            WebhookApplicationMapper webhookMapper
    ) {
        this.webhookRepository = webhookRepository;
        this.webhookMapper = webhookMapper;
    }

    @Override
    @Transactional
    public WebhookResponse createWebhook(WebhookRequest request) {
        logger.info("CreateWebhookUseCaseImpl: Creating new webhook - EndpointUrl: {}, EventType: {}",
                request.endpointUrl(), request.eventType());

        Webhook webhook = buildWebhookFromRequest(request);
        Webhook savedWebhook = webhookRepository.save(webhook);

        logger.info("CreateWebhookUseCaseImpl: Webhook created successfully - ID: {}", savedWebhook.id());

        return mapToResponse(savedWebhook);
    }

    private Webhook buildWebhookFromRequest(WebhookRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return webhookMapper.toDomain(request, WebhookStatus.ACTIVE, now, now);
    }

    private WebhookResponse mapToResponse(Webhook webhook) {
        return webhookMapper.toResponse(webhook);
    }
}

