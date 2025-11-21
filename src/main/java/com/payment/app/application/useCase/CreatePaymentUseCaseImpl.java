package com.payment.app.application.useCase;

import com.payment.app.application.dto.PaymentRequest;
import com.payment.app.application.dto.PaymentResponse;
import com.payment.app.application.mapper.PaymentApplicationMapper;
import com.payment.app.application.port.in.CreatePaymentUseCase;
import com.payment.app.application.port.out.CreditCardEncryptionPort;
import com.payment.app.application.port.out.EventPublisherPort;
import com.payment.app.application.port.out.PaymentRepositoryPort;
import com.payment.app.domain.exceptions.IdempotencyViolationException;
import com.payment.app.domain.model.Payment;
import com.payment.app.domain.type.PaymentStatus;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreatePaymentUseCaseImpl implements CreatePaymentUseCase {

    private final PaymentRepositoryPort repository;
    private final PaymentApplicationMapper mapper;
    private final CreditCardEncryptionPort creditCardEncryption;
    private final EventPublisherPort eventPublisher;

    public CreatePaymentUseCaseImpl(
            PaymentRepositoryPort repository,
            PaymentApplicationMapper mapper,
            CreditCardEncryptionPort creditCardEncryption,
            EventPublisherPort eventPublisher
    ) {
        this.mapper = mapper;
        this.repository = repository;
        this.creditCardEncryption = creditCardEncryption;
        this.eventPublisher = eventPublisher;
    }


    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest, String idempotencyKey) {

        Payment payment = mapper.toDomain(paymentRequest);

        Payment existingPayment = repository.findByIdempotencyKey(idempotencyKey);

        if (existingPayment != null) {
            throw new IdempotencyViolationException("Payment with the same idempotency key already exists.");
        }

        Payment response = repository.save(
                payment.toBuilder()
                        .status(PaymentStatus.CREATED)
                        .idempotencyKey(idempotencyKey)
                        .encryptedCardNumber(creditCardEncryption.encrypt(paymentRequest.cardNumber()))
                        .build()
        );

        eventPublisher.publishPaymentCreatedEvent("{ \"paymentId\": \"" + payment.firstName() + "\" }");

        return mapper.toResponse(response);

    }


}
