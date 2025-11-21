--  /post HTTP 200
INSERT INTO webhooks (id, endpoint_url, event_type, status)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'http://localhost:8083/post', -- URL que o Java vai chamar
    'PAYMENT_CREATED',
    'ACTIVE'
);

--  /status/500 HTTP 500
INSERT INTO webhooks (id, endpoint_url, event_type, status)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'http://localhost:8083/status/500',
    'PAYMENT_CREATED',
    'ACTIVE'
);

--  /delay/10 TIMEOUT 10 SECONDS 200
INSERT INTO webhooks (id, endpoint_url, event_type, status)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    'http://localhost:8083/delay/10',
    'PAYMENT_CREATED',
    'ACTIVE'
);

--  /status/400 HTTP 400
INSERT INTO webhooks (id, endpoint_url, event_type, status)
VALUES (
    '44444444-4444-4444-4444-444444444444',
    'http://localhost:8083/status/400',
    'PAYMENT_CREATED',
    'ACTIVE'
);