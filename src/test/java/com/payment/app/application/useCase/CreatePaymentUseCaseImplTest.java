package com.payment.app.application.useCase;

import com.payment.app.application.dto.CreatePaymentEvent;
import com.payment.app.application.dto.PaymentRequest;
import com.payment.app.application.dto.PaymentResponse;
import com.payment.app.application.mapper.PaymentApplicationMapper;
import com.payment.app.application.port.out.CreditCardEncryptionPort;
import com.payment.app.application.port.out.PaymentEventPublisherPort;
import com.payment.app.application.port.out.PaymentRepositoryPort;
import com.payment.app.domain.exceptions.IdempotencyViolationException;
import com.payment.app.domain.model.Payment;
import com.payment.app.domain.type.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreatePaymentUseCaseImpl Tests")
class CreatePaymentUseCaseImplTest {

    @Mock
    private PaymentRepositoryPort repository;

    @Mock
    private PaymentApplicationMapper mapper;

    @Mock
    private CreditCardEncryptionPort creditCardEncryption;

    @Mock
    private PaymentEventPublisherPort eventPublisher;

    @InjectMocks
    private CreatePaymentUseCaseImpl useCase;

    private PaymentRequest paymentRequest;
    private Payment payment;
    private Payment savedPayment;
    private PaymentResponse paymentResponse;
    private String idempotencyKey;
    private String encryptedCardNumber;

    @BeforeEach
    void setUp() {
        idempotencyKey = "idempotency-key-123";
        encryptedCardNumber = "encrypted-card-number";
        UUID paymentId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        paymentRequest = new PaymentRequest(
                "John",
                "Doe",
                "2000",
                "1234567812345678"
        );

        payment = new Payment(
                null,
                "John",
                "Doe",
                "2000",
                null,
                null,
                null,
                null
        );

        savedPayment = new Payment(
                paymentId,
                "John",
                "Doe",
                "2000",
                encryptedCardNumber,
                idempotencyKey,
                PaymentStatus.CREATED,
                now
        );

        paymentResponse = new PaymentResponse(
                paymentId,
                "John",
                "Doe",
                PaymentStatus.CREATED,
                now
        );
    }

    @Test
    @DisplayName("Should create payment successfully")
    void createPayment_shouldCreateSuccessfully() {
        when(mapper.toDomain(paymentRequest)).thenReturn(payment);
        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(null);
        when(creditCardEncryption.encrypt(paymentRequest.cardNumber())).thenReturn(encryptedCardNumber);
        when(repository.save(any(Payment.class))).thenReturn(savedPayment);
        when(mapper.toResponse(savedPayment)).thenReturn(paymentResponse);

        PaymentResponse result = useCase.createPayment(paymentRequest, idempotencyKey);

        assertThat(result).isNotNull();
        assertThat(result.paymentId()).isEqualTo(savedPayment.paymentId());
        assertThat(result.status()).isEqualTo(PaymentStatus.CREATED);
        verify(mapper).toDomain(paymentRequest);
        verify(repository).findByIdempotencyKey(idempotencyKey);
        verify(creditCardEncryption).encrypt(paymentRequest.cardNumber());
        verify(repository).save(any(Payment.class));
        verify(eventPublisher).publishPaymentCreatedEvent(any(CreatePaymentEvent.class));
        verify(mapper).toResponse(savedPayment);
    }

    @Test
    @DisplayName("Should throw exception when idempotency key already exists")
    void createPayment_shouldThrowException_whenIdempotencyKeyExists() {
        when(mapper.toDomain(paymentRequest)).thenReturn(payment);
        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(savedPayment);

        assertThatThrownBy(() -> useCase.createPayment(paymentRequest, idempotencyKey))
                .isInstanceOf(IdempotencyViolationException.class)
                .hasMessageContaining("Payment with the same idempotency key already exists");

        verify(mapper).toDomain(paymentRequest);
        verify(repository).findByIdempotencyKey(idempotencyKey);
        verify(creditCardEncryption, never()).encrypt(any());
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishPaymentCreatedEvent(any());
    }

    @Test
    @DisplayName("Should encrypt card number before saving")
    void createPayment_shouldEncryptCardNumber() {
        when(mapper.toDomain(paymentRequest)).thenReturn(payment);
        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(null);
        when(creditCardEncryption.encrypt(paymentRequest.cardNumber())).thenReturn(encryptedCardNumber);
        when(repository.save(any(Payment.class))).thenReturn(savedPayment);
        when(mapper.toResponse(savedPayment)).thenReturn(paymentResponse);

        useCase.createPayment(paymentRequest, idempotencyKey);

        verify(creditCardEncryption).encrypt(paymentRequest.cardNumber());
        verify(repository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should publish payment created event after saving")
    void createPayment_shouldPublishEvent() {
        when(mapper.toDomain(paymentRequest)).thenReturn(payment);
        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(null);
        when(creditCardEncryption.encrypt(paymentRequest.cardNumber())).thenReturn(encryptedCardNumber);
        when(repository.save(any(Payment.class))).thenReturn(savedPayment);
        when(mapper.toResponse(savedPayment)).thenReturn(paymentResponse);

        useCase.createPayment(paymentRequest, idempotencyKey);

        verify(eventPublisher).publishPaymentCreatedEvent(any(CreatePaymentEvent.class));
    }
}

