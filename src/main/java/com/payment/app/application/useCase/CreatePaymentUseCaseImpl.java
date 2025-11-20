package com.payment.app.application.useCase;

import com.payment.app.application.dto.PaymentRequest;
import com.payment.app.application.dto.PaymentResponse;
import com.payment.app.application.mapper.PaymentApplicationMapper;
import com.payment.app.application.port.in.CreatePaymentUseCase;
import com.payment.app.application.port.out.EncryptionStringPort;
import com.payment.app.application.port.out.PaymentRepositoryPort;
import com.payment.app.domain.model.Payment;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreatePaymentUseCaseImpl implements CreatePaymentUseCase {

    private final PaymentRepositoryPort repository;
    private final PaymentApplicationMapper mapper;
    private final EncryptionStringPort encryptionString;

    public CreatePaymentUseCaseImpl(
            PaymentRepositoryPort repository,
            PaymentApplicationMapper mapper,
            EncryptionStringPort encryptionString
    ) {
        this.mapper = mapper;
        this.repository = repository;
        this.encryptionString = encryptionString;
    }


    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest, String idempotencyKey) {

        Payment payment = mapper.toDomain(paymentRequest);

        Payment response = repository.save(
                payment.toBuilder()
                        .status("CREATED")
                        .idempotencyKey(idempotencyKey)
                        .encryptedCardNumber(encryptCardNumber(payment.encryptedCardNumber()))
                        .build()
        );

        return mapper.toResponse(response);

    }

    private String encryptCardNumber(String cardNumber) {
       return encryptionString.encrypt(cardNumber);
    }
}
