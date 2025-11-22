package com.payment.app.infrastructure.persistence.repository;

import com.payment.app.domain.model.Payment;
import com.payment.app.domain.type.PaymentStatus;
import com.payment.app.infrastructure.persistence.entity.PaymentEntity;
import com.payment.app.infrastructure.persistence.mapper.PaymentPersistenceMapper;
import com.payment.app.infrastructure.persistence.repository.jpa.PaymentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentRepositoryPortImpl Tests")
class PaymentRepositoryPortImplTest {

    @Mock
    private PaymentJpaRepository jpaRepository;

    @Mock
    private PaymentPersistenceMapper mapper;

    @InjectMocks
    private PaymentRepositoryPortImpl repository;

    private UUID paymentId;
    private Payment payment;
    private PaymentEntity paymentEntity;
    private String idempotencyKey;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        idempotencyKey = "idempotency-key-123";
        LocalDateTime now = LocalDateTime.now();

        payment = new Payment(
                paymentId,
                "John",
                "Doe",
                "2000",
                "encrypted-card",
                idempotencyKey,
                PaymentStatus.CREATED,
                now
        );

        paymentEntity = new PaymentEntity();
        paymentEntity.setId(paymentId);
        paymentEntity.setFirstName("John");
        paymentEntity.setLastName("Doe");
        paymentEntity.setZipCode("2000");
        paymentEntity.setEncryptedCardNumber("encrypted-card");
        paymentEntity.setIdempotencyKey(idempotencyKey);
        paymentEntity.setStatus(PaymentStatus.CREATED);
        paymentEntity.setCreatedAt(now);
    }

    @Test
    @DisplayName("Should save payment successfully")
    void save_shouldSaveSuccessfully() {
        when(mapper.toEntity(payment)).thenReturn(paymentEntity);
        when(jpaRepository.saveAndFlush(paymentEntity)).thenReturn(paymentEntity);
        when(mapper.toDomain(paymentEntity)).thenReturn(payment);

        Payment result = repository.save(payment);

        assertThat(result).isNotNull();
        assertThat(result.paymentId()).isEqualTo(paymentId);
        verify(mapper).toEntity(payment);
        verify(jpaRepository).saveAndFlush(paymentEntity);
        verify(mapper).toDomain(paymentEntity);
    }

    @Test
    @DisplayName("Should find payment by idempotency key when exists")
    void findByIdempotencyKey_shouldReturnPayment_whenExists() {
        when(jpaRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(paymentEntity));
        when(mapper.toDomain(paymentEntity)).thenReturn(payment);

        Payment result = repository.findByIdempotencyKey(idempotencyKey);

        assertThat(result).isNotNull();
        assertThat(result.paymentId()).isEqualTo(paymentId);
        assertThat(result.idempotencyKey()).isEqualTo(idempotencyKey);
        verify(jpaRepository).findByIdempotencyKey(idempotencyKey);
        verify(mapper).toDomain(paymentEntity);
    }

    @Test
    @DisplayName("Should return null when payment not found by idempotency key")
    void findByIdempotencyKey_shouldReturnNull_whenNotExists() {
        when(jpaRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());

        Payment result = repository.findByIdempotencyKey(idempotencyKey);

        assertThat(result).isNull();
        verify(jpaRepository).findByIdempotencyKey(idempotencyKey);
        verify(mapper, never()).toDomain(any());
    }
}

