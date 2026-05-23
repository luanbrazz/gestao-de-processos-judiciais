-- =============================================
-- V7__refactor_parte_heranca_jpa.sql
-- Refatora tabela parte para suportar herança JPA (JOINED)
-- Detecta CPF (11 dígitos) vs CNPJ (14 dígitos) nos dados existentes
-- =============================================

-- =============================================
-- 1. Adicionar coluna discriminator na tabela parte
-- =============================================
ALTER TABLE parte
    ADD COLUMN IF NOT EXISTS tipo_pessoa VARCHAR(20) NOT NULL DEFAULT 'PESSOA_FISICA';

-- =============================================
-- 2. Atualizar o discriminator baseado no documento existente
--    CPF:  11 dígitos numéricos → PESSOA_FISICA
--    CNPJ: 14 dígitos numéricos → PESSOA_JURIDICA
-- =============================================
UPDATE parte
SET tipo_pessoa = 'PESSOA_JURIDICA'
WHERE length(regexp_replace(documento, '[^0-9]', '', 'g')) = 14;

UPDATE parte
SET tipo_pessoa = 'PESSOA_FISICA'
WHERE length(regexp_replace(documento, '[^0-9]', '', 'g')) = 11;

-- =============================================
-- 3. Criar tabela pessoa_fisica
-- =============================================
CREATE TABLE IF NOT EXISTS pessoa_fisica
(
    id              UUID         NOT NULL PRIMARY KEY REFERENCES parte (id) ON DELETE CASCADE,
    documento       VARCHAR(20)  NOT NULL,
    data_nascimento DATE,
    cep             VARCHAR(10),
    logradouro      VARCHAR(255),
    bairro          VARCHAR(255),
    cidade          VARCHAR(255),
    uf              VARCHAR(2)
);

-- =============================================
-- 4. Criar tabela pessoa_juridica
-- =============================================
CREATE TABLE IF NOT EXISTS pessoa_juridica
(
    id                UUID         NOT NULL PRIMARY KEY REFERENCES parte (id) ON DELETE CASCADE,
    documento         VARCHAR(20)  NOT NULL,
    razao_social      VARCHAR(255),
    cnae              VARCHAR(255),
    natureza_juridica VARCHAR(255),
    situacao          VARCHAR(100),
    cep               VARCHAR(10),
    logradouro        VARCHAR(255),
    bairro            VARCHAR(255),
    cidade            VARCHAR(255),
    uf                VARCHAR(2)
);

-- =============================================
-- 5. Migrar CPFs → pessoa_fisica
-- =============================================
INSERT INTO pessoa_fisica (id, documento, cep, logradouro, bairro, cidade, uf)
SELECT p.id,
       p.documento,
       p.cep,
       p.logradouro,
       p.bairro,
       p.cidade,
       p.uf
FROM parte p
WHERE p.tipo_pessoa = 'PESSOA_FISICA'
  AND NOT EXISTS (
      SELECT 1 FROM pessoa_fisica pf WHERE pf.id = p.id
  );

-- =============================================
-- 6. Migrar CNPJs → pessoa_juridica
--    nome da parte vira razao_social por padrão
-- =============================================
INSERT INTO pessoa_juridica (id, documento, razao_social, cep, logradouro, bairro, cidade, uf)
SELECT p.id,
       p.documento,
       p.nome,
       p.cep,
       p.logradouro,
       p.bairro,
       p.cidade,
       p.uf
FROM parte p
WHERE p.tipo_pessoa = 'PESSOA_JURIDICA'
  AND NOT EXISTS (
      SELECT 1 FROM pessoa_juridica pj WHERE pj.id = p.id
  );

-- =============================================
-- 7. Remover colunas que migraram para subtabelas
-- =============================================
ALTER TABLE parte DROP COLUMN IF EXISTS documento;
ALTER TABLE parte DROP COLUMN IF EXISTS cep;
ALTER TABLE parte DROP COLUMN IF EXISTS logradouro;
ALTER TABLE parte DROP COLUMN IF EXISTS bairro;
ALTER TABLE parte DROP COLUMN IF EXISTS cidade;
ALTER TABLE parte DROP COLUMN IF EXISTS uf;

-- =============================================
-- 8. Remover default do discriminator
-- =============================================
ALTER TABLE parte ALTER COLUMN tipo_pessoa DROP DEFAULT;

-- =============================================
-- 9. Índices para performance
-- =============================================
CREATE INDEX IF NOT EXISTS idx_pessoa_fisica_documento ON pessoa_fisica(documento);
CREATE INDEX IF NOT EXISTS idx_pessoa_juridica_documento ON pessoa_juridica(documento);

SELECT 'Migração V7 — herança JPA concluída com sucesso!' AS status;
