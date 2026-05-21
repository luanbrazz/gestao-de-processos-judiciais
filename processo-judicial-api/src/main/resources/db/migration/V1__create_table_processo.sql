CREATE TABLE processo
(
    id            BIGSERIAL PRIMARY KEY,
    numero        VARCHAR(20)  NOT NULL UNIQUE,
    assunto       VARCHAR(255) NOT NULL,
    vara          VARCHAR(255) NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'ATIVO',
    data_abertura DATE         NOT NULL,
    criado_em     TIMESTAMP    NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP    NOT NULL DEFAULT NOW()
);