package com.payment.app.application.useCase;

import com.payment.app.application.dto.PaymentRequest;
import com.payment.app.application.dto.PaymentResponse;
import com.payment.app.application.mapper.PaymentApplicationMapper;
import com.payment.app.application.port.in.CreatePaymentUseCase;
import com.payment.app.application.port.out.PaymentRepository;
import com.payment.app.domain.model.Payment;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CreatePaymentUseCaseImpl implements CreatePaymentUseCase {

    private final PaymentRepository repository;
    private final PaymentApplicationMapper mapper;

    public CreatePaymentUseCaseImpl(PaymentRepository repository, PaymentApplicationMapper mapper) {
        this.mapper = mapper;
        this.repository = repository;
    }


    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest, String idempotencyKey) {

        Payment payment = mapper.toDomain(paymentRequest);

        payment = payment.withIdempotencyKey(idempotencyKey);
        String encryptedCardNumber = encryptCardNumber(paymentRequest.cardNumber());
        payment = payment.withEncryptedCardNumber(encryptedCardNumber);
        payment = payment.withStatus("CREATED");

        Payment response = repository.save(payment);

        return mapper.toResponse(response);

    }

    private String encryptCardNumber(String cardNumber) {
        return "*****1234";
    }
}
