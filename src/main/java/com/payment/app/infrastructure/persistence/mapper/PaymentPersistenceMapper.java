package com.payment.app.infrastructure.persistence.mapper;

import com.payment.app.domain.model.Payment;
import com.payment.app.infrastructure.persistence.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentPersistenceMapper {
    @Mapping(target = "id", ignore = true)
    PaymentEntity toEntity(Payment payment);

    @Mapping(target = "paymentId", source = "id")
    Payment toDomain(PaymentEntity entity);
}
