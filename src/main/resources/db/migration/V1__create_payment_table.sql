CREATE TABLE payments (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    encrypted_card_number VARCHAR(255) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    idempotency_key VARCHAR(36) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_payment PRIMARY KEY (id)
);

CREATE INDEX idx_idempotency_key ON payments(idempotency_key);


CREATE TABLE webhooks (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    endpoint_url VARCHAR(255) NOT NULL,
    secret_key VARCHAR(255),
    event_type VARCHAR(50) NOT NULL DEFAULT 'PAYMENT_CREATED',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_webhook PRIMARY KEY (id)
);