package com.payment.app.application.useCase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.app.application.dto.PaymentWebhookProcessEvent;
import com.payment.app.application.dto.WebhookPayloadDto;
import com.payment.app.application.dto.WebhookResponse;
import com.payment.app.application.port.in.ProcessWebhookDeliveryUseCase;
import com.payment.app.application.port.out.WebhookClientPort;
import com.payment.app.application.port.out.WebhookDeliveryLogRepositoryPort;
import com.payment.app.application.port.out.WebhookRepositoryPort;
import com.payment.app.domain.model.Webhook;
import com.payment.app.domain.model.WebhookDeliveryLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProcessWebhookDeliveryUseCaseImpl implements ProcessWebhookDeliveryUseCase {

    private final WebhookRepositoryPort webhookRepository;
    private final WebhookDeliveryLogRepositoryPort webhookDeliveryLogRepository;
    private final WebhookClientPort webhookClient;
    private static final Logger logger = LoggerFactory.getLogger(ProcessWebhookDeliveryUseCaseImpl.class);
    private final ObjectMapper objectMapper;

    public ProcessWebhookDeliveryUseCaseImpl(
            WebhookRepositoryPort webhookRepository,
            WebhookDeliveryLogRepositoryPort webhookDeliveryLogRepository,
            WebhookClientPort webhookClient,
            ObjectMapper objectMapper
    ) {
        this.webhookRepository = webhookRepository;
        this.webhookDeliveryLogRepository = webhookDeliveryLogRepository;
        this.webhookClient = webhookClient;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void processDelivery(PaymentWebhookProcessEvent event) {
        logger.info("ProcessWebhookDeliveryUseCaseImpl: Processing webhook delivery - PaymentId: {}, WebhookId: {}",
                event.paymentId(), event.webhookId());

        UUID paymentId = event.paymentId();
        UUID webhookId = event.webhookId();

        Webhook webhook = findWebhookOrThrow(webhookId);
        WebhookDeliveryLog deliveryLog = findDeliveryLog(paymentId, webhookId);

        if (isAlreadyDeliveredSuccessfully(deliveryLog)) {
            logger.info("ProcessWebhookDeliveryUseCaseImpl: Webhook already delivered successfully. Skipping.");
            return;
        }

        try {
            ensureDeliveryLogExists(deliveryLog, paymentId, webhookId);
            incrementAttemptCounter(paymentId, webhookId);

            String payloadJson = buildPayloadJson(paymentId);
            WebhookResponse response = webhookClient.postEvent(webhook.endpointUrl(), payloadJson);

            handleWebhookResponse(response, paymentId, webhookId, webhook.endpointUrl());

        } catch (Exception e) {
            handleDeliveryError(e, paymentId, webhookId);
        }
    }

    private Webhook findWebhookOrThrow(UUID webhookId) {
        Webhook webhook = webhookRepository.findById(webhookId);
        if (webhook == null) {
            throw new RuntimeException("ProcessWebhookDeliveryUseCaseImpl: Webhook not found with id: " + webhookId);
        }
        return webhook;
    }

    private WebhookDeliveryLog findDeliveryLog(UUID paymentId, UUID webhookId) {
        return webhookDeliveryLogRepository.findByPaymentIdAndWebhookId(paymentId, webhookId);
    }

    private boolean isAlreadyDeliveredSuccessfully(WebhookDeliveryLog deliveryLog) {
        return deliveryLog != null && "SUCCESS".equals(deliveryLog.status());
    }

    private void ensureDeliveryLogExists(WebhookDeliveryLog deliveryLog, UUID paymentId, UUID webhookId) {
        if (deliveryLog == null) {
            webhookDeliveryLogRepository.saveLogAsPending(paymentId, webhookId);
            logger.debug("ProcessWebhookDeliveryUseCaseImpl: Created new delivery log entry");
        }
    }

    private void incrementAttemptCounter(UUID paymentId, UUID webhookId) {
        webhookDeliveryLogRepository.incrementAttemptCount(paymentId, webhookId);
        logger.debug("Incremented attempt counter");
    }

    private String buildPayloadJson(UUID paymentId) {
        try {
            WebhookPayloadDto payload = new WebhookPayloadDto(paymentId, "PAYMENT_COMPLETED");
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            logger.error("ProcessWebhookDeliveryUseCaseImpl: Failed to serialize webhook payload: {}", e.getMessage());
            throw new RuntimeException("Failed to build webhook payload", e);
        }
    }

    private void handleWebhookResponse(WebhookResponse response, UUID paymentId, UUID webhookId, String endpointUrl) {
        if (response.isSuccess()) {
            handleSuccess(response, paymentId, webhookId, endpointUrl);
        } else if (response.isPermanentError()) {
            handlePermanentError(response, paymentId, webhookId);
        } else {
            handleTransientError(response);
        }
    }

    private void handleSuccess(WebhookResponse response, UUID paymentId, UUID webhookId, String endpointUrl) {
        webhookDeliveryLogRepository.updateStatusSuccess(paymentId, webhookId, response.statusCode());
        logger.info(
                "ProcessWebhookDeliveryUseCaseImpl: Webhook delivered successfully to {} - HTTP {}",
                endpointUrl,
                response.statusCode()
        );
    }

    private void handlePermanentError(WebhookResponse response, UUID paymentId, UUID webhookId) {
        webhookDeliveryLogRepository.updateStatusFailed(paymentId, webhookId, response.statusCode());
        logger.warn(
                "ProcessWebhookDeliveryUseCaseImpl Permanent error (HTTP {}). Marked as FAILED without retry.",
                response.statusCode()
        );
    }

    private void handleTransientError(WebhookResponse response) {
        logger.error("ProcessWebhookDeliveryUseCaseImpl: Transient error (HTTP {}). Will retry.", response.statusCode());
        throw new RuntimeException("Transient error " + response.statusCode() + " from webhook endpoint");
    }

    private void handleDeliveryError(Exception e, UUID paymentId, UUID webhookId) {
        logger.error(
                "ProcessWebhookDeliveryUseCaseImpl: Error processing webhook delivery - PaymentId: {}, WebhookId: {}. Error: {}",
                paymentId,
                webhookId,
                e.getMessage()
        );
        throw new RuntimeException("Connection failure", e);
    }
}
