package com.payment.app.infrastructure.persistence.entity;

import com.payment.app.domain.type.WebhookDeliveryLogStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhook_delivery_log")
public class WebhookDeliveryLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @Column(name = "payment_id", nullable = false, length = 36)
    private String paymentId;

    @Column(name = "webhook_id", nullable = false, length = 36)
    private String webhookId;

    @Column(name = "status", nullable = false, length = 20)
    private WebhookDeliveryLogStatus status;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;

    @Column(name = "response_code")
    private Integer responseCode;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getWebhookId() {
        return this.webhookId;
    }

    public void setWebhookId(String webhook) {
        this.webhookId = webhook;
    }

    public WebhookDeliveryLogStatus getStatus() {
        return status;
    }

    public void setStatus(WebhookDeliveryLogStatus status) {
        this.status = status;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public LocalDateTime getLastAttemptAt() {
        return lastAttemptAt;
    }

    public void setLastAttemptAt(LocalDateTime lastAttemptAt) {
        this.lastAttemptAt = lastAttemptAt;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }




}
