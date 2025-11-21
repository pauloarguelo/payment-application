package com.payment.app.infrastructure.persistence.mapper;

import com.payment.app.domain.model.Webhook;
import com.payment.app.infrastructure.persistence.entity.WebhookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WebhookPersistenceMapper {

    @Mapping(target = "eventType", source = "eventType")
    Webhook toDomain(WebhookEntity entity);

    List<Webhook> toDomain(List<WebhookEntity> entities);

    @Mapping(target = "eventType", expression = "java(webhook.eventType() != null ? webhook.eventType().name() : null)")
    WebhookEntity toEntity(Webhook webhook);
}
