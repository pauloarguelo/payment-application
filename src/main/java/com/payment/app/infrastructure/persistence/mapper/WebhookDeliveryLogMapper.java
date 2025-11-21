package com.payment.app.infrastructure.persistence.mapper;

import com.payment.app.domain.model.WebhookDeliveryLog;
import com.payment.app.infrastructure.persistence.entity.WebhookDeliveryLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WebhookDeliveryLogMapper {
    WebhookDeliveryLogEntity toEntity(WebhookDeliveryLog domain);
    WebhookDeliveryLog toDomain(WebhookDeliveryLogEntity entity);
}