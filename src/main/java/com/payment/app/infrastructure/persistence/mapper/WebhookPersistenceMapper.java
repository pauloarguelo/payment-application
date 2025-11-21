package com.payment.app.infrastructure.persistence.mapper;

import com.payment.app.domain.model.Webhook;
import com.payment.app.infrastructure.persistence.entity.WebhookEntity;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface WebhookPersistenceMapper {

    Webhook toDomain(WebhookEntity entity);

    List<Webhook> toDomain(List<WebhookEntity> entities);
}
