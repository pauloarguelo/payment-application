package com.payment.app.infrastructure.persistence.repository;

import com.payment.app.application.port.out.PaymentRepositoryPort;
import com.payment.app.domain.model.Payment;
import com.payment.app.infrastructure.persistence.entity.PaymentEntity;
import com.payment.app.infrastructure.persistence.mapper.PaymentPersistenceMapper;
import org.springframework.stereotype.Repository;


@Repository
public class PaymentRepositoryImpl implements PaymentRepositoryPort {

    private final PaymentJpaRepository jpaRepository;
    private final PaymentPersistenceMapper mapper;

    public PaymentRepositoryImpl(PaymentJpaRepository jpaRepository, PaymentPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Payment save(Payment payment) {

        PaymentEntity entity = mapper.toEntity(payment);
        PaymentEntity savedEntity = jpaRepository.saveAndFlush(entity);

        return new Payment(
                savedEntity.getId(),
                savedEntity.getFirstName(),
                savedEntity.getLastName(),
                savedEntity.getZipCode(),
                savedEntity.getEncryptedCardNumber(),
                savedEntity.getIdempotencyKey(),
                savedEntity.getStatus(),
                savedEntity.getCreatedAt()
        );

    }
}
