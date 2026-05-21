CREATE TABLE movimentacao
(
    id                BIGSERIAL PRIMARY KEY,
    processo_id       BIGINT    NOT NULL REFERENCES processo (id),
    descricao         TEXT      NOT NULL,
    data_movimentacao TIMESTAMP NOT NULL DEFAULT NOW()
);