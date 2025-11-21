package com.payment.app.infrastructure.messaging.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.app.application.dto.PaymentCreateEvent;
import com.payment.app.application.dto.PaymentWebhookProcessEvent;
import com.payment.app.application.port.in.ProcessWebhookDeliveryUseCase;
import com.payment.app.infrastructure.messaging.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentWebhookDeliveryEventListener {

    private final ProcessWebhookDeliveryUseCase useCase;
    private static final Logger logger = LoggerFactory.getLogger(PaymentWebhookDeliveryEventListener.class);
    private final ObjectMapper objectMapper;

    public PaymentWebhookDeliveryEventListener(ObjectMapper objectMapper, ProcessWebhookDeliveryUseCase useCase) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.DELIVERY_QUEUE_NAME)
    public void processEvent(String eventPayload) {

        logger.info("PaymentWebhookDeliveryEventListener: Received event payload: {}", eventPayload);

        try {
            PaymentWebhookProcessEvent event = objectMapper.readValue(eventPayload, PaymentWebhookProcessEvent.class);
            this.useCase.processDelivery(event);
        }catch (Exception e) {
            logger.warn("PaymentWebhookDeliveryEventListener: Failed to process event payload: {}", eventPayload, e);
            throw new RuntimeException(e);
        }

    }
}
