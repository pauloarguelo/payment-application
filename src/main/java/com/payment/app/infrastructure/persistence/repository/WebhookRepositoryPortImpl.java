package com.payment.app.infrastructure.persistence.repository;

import com.payment.app.application.port.out.WebhookRepositoryPort;
import com.payment.app.domain.model.Webhook;
import com.payment.app.infrastructure.persistence.entity.WebhookEntity;
import com.payment.app.infrastructure.persistence.mapper.WebhookPersistenceMapper;
import com.payment.app.infrastructure.persistence.repository.jpa.WebhookJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class WebhookRepositoryPortImpl implements WebhookRepositoryPort {

    private final WebhookJpaRepository jpaRepository;
    private final WebhookPersistenceMapper mapper;


    public WebhookRepositoryPortImpl(WebhookJpaRepository jpaRepository, WebhookPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }


    @Override
    public Webhook findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public Webhook save(Webhook webhook) {
        return null;
    }

    @Override
    public List<Webhook> findByStatus(String status) {
        List<WebhookEntity> entities = jpaRepository.findByStatus(status);
        return mapper.toDomain(entities);
    }
}
