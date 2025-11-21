package com.payment.app.application.useCase;

import com.payment.app.application.dto.PaymentCreateEvent;
import com.payment.app.application.dto.PaymentWebhookProcessEvent;
import com.payment.app.application.port.in.WebhookDeliveryUseCase;
import com.payment.app.application.port.out.PaymentWebhookEventPublisherPort;
import com.payment.app.application.port.out.WebhookRepositoryPort;
import com.payment.app.domain.model.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebhookDeliveryUseCaseImpl implements WebhookDeliveryUseCase {

    private final WebhookRepositoryPort webhookRepositoryPort;
    private static final Logger logger = LoggerFactory.getLogger(WebhookDeliveryUseCaseImpl.class);
    private final PaymentWebhookEventPublisherPort publisherPort;

    public WebhookDeliveryUseCaseImpl(
            WebhookRepositoryPort webhookRepositoryPort,
            PaymentWebhookEventPublisherPort publisherPort
    ) {
        this.publisherPort = publisherPort;
        this.webhookRepositoryPort = webhookRepositoryPort;
    }


    @Override
    public void sendPaymentAndWebhooksToProcess(PaymentCreateEvent event) {

        logger.info("WebhookDeliveryUseCaseImpl: Sending payment and webhooks to process for event: {}", event);

        List<Webhook> webhooks = webhookRepositoryPort.findByStatus("ACTIVE");

        for (Webhook webhook : webhooks) {

            logger.info("Sending webhook: {} for payment event: {}", webhook, event);

            PaymentWebhookProcessEvent paymentWebhookProcessEvent = new PaymentWebhookProcessEvent(
                  event.paymentId(), webhook.id()
            );

            this.publisherPort.publishPaymentWebhookProcessEvent(paymentWebhookProcessEvent);

        }

    }

}
