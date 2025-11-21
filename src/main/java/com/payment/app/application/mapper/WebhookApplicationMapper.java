package com.payment.app.application.mapper;

import com.payment.app.application.dto.WebhookRequest;
import com.payment.app.application.dto.WebhookResponse;
import com.payment.app.domain.model.Webhook;
import com.payment.app.domain.type.WebhookStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface WebhookApplicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "secretKey", ignore = true)
    Webhook toDomain(WebhookRequest request, WebhookStatus status, LocalDateTime createdAt, LocalDateTime updatedAt);


    WebhookResponse toResponse(Webhook webhook);
}

