package com.payment.app.infrastructure.messaging;

import com.payment.app.infrastructure.messaging.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EventListener {


    @RabbitListener(queues = RabbitMQConfig.WEBHOOK_QUEUE_NAME)
    public void processEvent(String eventPayload) {
        // Logic to process the incoming event
        System.out.println("Received event: " + eventPayload);
    }
}
