package com.payment.app.application.mapper;

import com.payment.app.application.dto.PaymentRequest;
import com.payment.app.application.dto.PaymentResponse;
import com.payment.app.domain.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentApplicationMapper {

    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "idempotencyKey", ignore = true)
    @Mapping(target = "encryptedCardNumber", ignore = true)
    @Mapping(target = "withIdempotencyKey", ignore = true)
    @Mapping(target = "withEncryptedCardNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "withStatus", ignore = true)
    Payment toDomain(PaymentRequest paymentApplication);

    PaymentResponse toResponse(Payment payment);
}
