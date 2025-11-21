package com.payment.app.infrastructure.messaging.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.app.application.dto.PaymentCreateEvent;
import com.payment.app.application.port.in.WebhookDeliveryUseCase;
import com.payment.app.infrastructure.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    private final WebhookDeliveryUseCase webhookDeliveryUseCase;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(PaymentEventListener.class);


    public PaymentEventListener(ObjectMapper objectMapper, WebhookDeliveryUseCase webhookDeliveryUseCase) {
        this.webhookDeliveryUseCase = webhookDeliveryUseCase;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.MAIN_QUEUE_NAME)
    public void processEvent(String eventPayload) {

        logger.info("PaymentEventListener: Received event payload: {}", eventPayload);

        try {
            PaymentCreateEvent event = objectMapper.readValue(eventPayload, PaymentCreateEvent.class);
            this.webhookDeliveryUseCase.sendPaymentAndWebhooksToProcess(event);
        }catch (Exception e) {
            logger.warn("PaymentEventListener: Failed to process event payload: {}", eventPayload, e);
            throw new RuntimeException(e);
        }

    }

}
