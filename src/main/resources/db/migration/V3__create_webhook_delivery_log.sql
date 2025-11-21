CREATE TABLE webhook_delivery_log (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    payment_id CHAR(36) NOT NULL,
    webhook_id CHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    attempt_count INT DEFAULT 0,
    last_attempt_at TIMESTAMP,
    response_code INT,

    CONSTRAINT pk_delivery_log PRIMARY KEY (id),

    CONSTRAINT uq_payment_webhook UNIQUE (payment_id, webhook_id),

    CONSTRAINT fk_payment FOREIGN KEY (payment_id) REFERENCES payments(id),
    CONSTRAINT fk_webhook FOREIGN KEY (webhook_id) REFERENCES webhooks(id)
);