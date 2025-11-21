package com.payment.app.application.useCase;

import com.payment.app.application.dto.PaymentRequest;
import com.payment.app.application.dto.PaymentResponse;
import com.payment.app.application.mapper.PaymentApplicationMapper;
import com.payment.app.application.port.in.CreatePaymentUseCase;
import com.payment.app.application.port.out.CreditCardEncryptionPort;
import com.payment.app.application.port.out.PaymentRepositoryPort;
import com.payment.app.domain.model.Payment;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreatePaymentUseCaseImpl implements CreatePaymentUseCase {

    private final PaymentRepositoryPort repository;
    private final PaymentApplicationMapper mapper;
    private final CreditCardEncryptionPort creditCardEncryption;

    public CreatePaymentUseCaseImpl(
            PaymentRepositoryPort repository,
            PaymentApplicationMapper mapper,
            CreditCardEncryptionPort creditCardEncryption
    ) {
        this.mapper = mapper;
        this.repository = repository;
        this.creditCardEncryption = creditCardEncryption;
    }


    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest, String idempotencyKey) {

        Payment payment = mapper.toDomain(paymentRequest);

        Payment response = repository.save(
                payment.toBuilder()
                        .status("CREATED")
                        .idempotencyKey(idempotencyKey)
                        .encryptedCardNumber(creditCardEncryption.encrypt(paymentRequest.cardNumber()))
                        .build()
        );

        return mapper.toResponse(response);

    }


}
