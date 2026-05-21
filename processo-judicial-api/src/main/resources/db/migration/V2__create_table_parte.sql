CREATE TABLE parte
(
    id          BIGSERIAL PRIMARY KEY,
    processo_id BIGINT       NOT NULL REFERENCES processo (id),
    tipo        VARCHAR(10)  NOT NULL,
    nome        VARCHAR(255) NOT NULL,
    documento   VARCHAR(20)  NOT NULL,
    cep         VARCHAR(10),
    logradouro  VARCHAR(255),
    bairro      VARCHAR(255),
    cidade      VARCHAR(255),
    uf          VARCHAR(2),
    criado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);