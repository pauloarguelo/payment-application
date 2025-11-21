-- 1. CENÁRIO DE SUCESSO (Happy Path)
-- O endpoint /post retorna 200 OK e o corpo que você enviou.
INSERT INTO webhooks (id, endpoint_url, event_type, status)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'http://localhost:8083/post', -- URL que o Java vai chamar
    'PAYMENT_CREATED',
    'ACTIVE'
);

-- 2. CENÁRIO DE RETRY (Falha Transiente)
-- O endpoint /status/500 sempre retorna HTTP 500.
-- Isso deve disparar sua Exception e ativar o Spring Retry.
INSERT INTO webhooks (id, endpoint_url, event_type, status)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'http://localhost:8083/status/500',
    'PAYMENT_CREATED',
    'ACTIVE'
);

-- 3. CENÁRIO DE TIMEOUT (Opcional)
-- O endpoint /delay/10 demora 10 segundos para responder.
-- Se seu RestClient estiver com timeout de 5s, vai dar erro e retentar.
INSERT INTO webhooks (id, endpoint_url, event_type, status)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    'http://localhost:8083/delay/10',
    'PAYMENT_CREATED',
    'ACTIVE'
);