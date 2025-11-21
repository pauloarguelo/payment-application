package com.payment.app.application.useCase;

import com.payment.app.application.dto.PaymentWebhookProcessEvent;
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

import java.util.Objects;
import java.util.UUID;

@Service
public class ProcessWebhookDeliveryUseCaseImpl implements ProcessWebhookDeliveryUseCase {

    private final WebhookRepositoryPort webhookRepository;
    private final WebhookDeliveryLogRepositoryPort webhookDeliveryLogRepository;
    private final WebhookClientPort webhookClient;
    private static final Logger logger = LoggerFactory.getLogger(ProcessWebhookDeliveryUseCaseImpl.class);

    public ProcessWebhookDeliveryUseCaseImpl(
            WebhookRepositoryPort webhookRepository,
            WebhookDeliveryLogRepositoryPort webhookDeliveryLogRepository,
            WebhookClientPort webhookClient
    ) {
        this.webhookRepository = webhookRepository;
        this.webhookDeliveryLogRepository = webhookDeliveryLogRepository;
        this.webhookClient = webhookClient;
    }

    @Override
    @Transactional
    public void processDelivery(PaymentWebhookProcessEvent event) {
        logger.info("ProcessWebhookDeliveryUseCaseImpl: Processing webhook delivery for event: {}", event);

        UUID paymentId = event.paymentId();
        UUID webhookId = event.webhookId();

        Webhook webhook = webhookRepository.findById(webhookId);

        if(webhook == null) {
            throw new RuntimeException("Webhook not found with id: " + webhookId);
        }

        WebhookDeliveryLog log = this.webhookDeliveryLogRepository.findByPaymentIdAndWebhookId(
                paymentId,
                webhookId
        );

        if(log != null && Objects.equals(log.status(), "SUCCESS")) {
            logger.info("Delivery log already exists for paymentId: {} and webhookId: {}. Skipping delivery.",
                    paymentId, webhookId);
            return;
        }

        try {

            this.webhookDeliveryLogRepository.saveLogAsPending(paymentId, webhookId);

            String payloadJson = "{\"paymentId\": \"" + paymentId + "\", \"event\": \"PAYMENT_CREATED\"}";

            WebhookResponse response = webhookClient.postEvent(webhook.endpointUrl(), payloadJson);

            if (response.isSuccess()) {
                webhookDeliveryLogRepository.updateStatusSuccess(paymentId, webhookId, response.statusCode());
                logger.info("Webhook delivered successfully to {}", webhook.endpointUrl());
            } else if (response.isPermanentError()) {
                webhookDeliveryLogRepository.updateStatusFailed(paymentId, webhookId, response.statusCode());
                logger.warn("Permanent failure delivering webhook ({}). Marking as FAILED.", response.statusCode());
            } else {
                throw new RuntimeException("Transient error " + response.statusCode() + " from webhook endpoint");
            }

        }catch (Exception e) {

            logger.error("Error processing webhook delivery for paymentId: {} and webhookId: {}. Error: {}",
                    paymentId, webhookId, e.getMessage());

            throw new RuntimeException("Connection failure", e);
        }

    }
}
