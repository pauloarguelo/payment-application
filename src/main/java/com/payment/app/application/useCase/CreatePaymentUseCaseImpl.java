package com.payment.app.application.useCase;

import com.payment.app.application.dto.PaymentRequest;
import com.payment.app.application.dto.PaymentResponse;
import com.payment.app.application.port.in.CreatePaymentUseCase;
import com.payment.app.application.port.out.PaymentRepository;
import com.payment.app.domain.model.Payment;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CreatePaymentUseCaseImpl implements CreatePaymentUseCase {

    private final PaymentRepository repository;

    public CreatePaymentUseCaseImpl(PaymentRepository repository) {
        this.repository = repository;
    }


    @Override
    public PaymentResponse createPayment(PaymentRequest payment, String idempotencyKey) {

        Payment newPayment = new Payment(
                java.util.UUID.randomUUID(),
                payment.firstName(),
                payment.lastName(),
                payment.zipCode(),
                payment.cardNumber()
        );
        Payment response = repository.save(newPayment);

       return new PaymentResponse(
               response.paymentId(),
               response.firstName(),
               response.lastName(),
               LocalDateTime.now()
       );

    }
}
