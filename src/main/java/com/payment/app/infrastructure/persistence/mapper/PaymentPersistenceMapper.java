package com.payment.app.infrastructure.persistence.mapper;

import com.payment.app.domain.model.Payment;
import com.payment.app.infrastructure.persistence.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentPersistenceMapper {
    PaymentEntity toEntity(Payment payment);
}
