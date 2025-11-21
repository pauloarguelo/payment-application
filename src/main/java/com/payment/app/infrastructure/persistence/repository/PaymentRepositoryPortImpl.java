package com.payment.app.infrastructure.persistence.repository;

import com.payment.app.application.port.out.PaymentRepositoryPort;
import com.payment.app.domain.model.Payment;
import com.payment.app.infrastructure.persistence.entity.PaymentEntity;
import com.payment.app.infrastructure.persistence.mapper.PaymentPersistenceMapper;
import com.payment.app.infrastructure.persistence.repository.jpa.PaymentJpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public class PaymentRepositoryPortImpl implements PaymentRepositoryPort {

    private final PaymentJpaRepository jpaRepository;
    private final PaymentPersistenceMapper mapper;

    public PaymentRepositoryPortImpl(PaymentJpaRepository jpaRepository, PaymentPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Payment save(Payment payment) {

        PaymentEntity entity = mapper.toEntity(payment);
        PaymentEntity savedEntity = jpaRepository.saveAndFlush(entity);
        return mapper.toDomain(savedEntity);

    }

    @Override
    public Payment findByIdempotencyKey(String idempotencyKey) {
        return jpaRepository.findByIdempotencyKey(idempotencyKey)
                .map(mapper::toDomain)
                .orElse(null);
    }
}
