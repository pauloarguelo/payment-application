CREATE TABLE payments (
    id VARCHAR(36) NOT NULL,
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