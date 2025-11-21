package com.payment.app.application.useCase;

import com.payment.app.application.dto.PaymentWebhookProcessEvent;
import com.payment.app.application.port.in.ProcessWebhookDeliveryUseCase;
import org.springframework.stereotype.Service;

@Service
public class ProcessWebhookDeliveryUseCaseImpl implements ProcessWebhookDeliveryUseCase {
    @Override
    public void processDelivery(PaymentWebhookProcessEvent event) {
        System.out.println("Request POST event via HTTP CLIENT: " + event);
    }
}
