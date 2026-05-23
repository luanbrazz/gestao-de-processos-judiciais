-- =============================================
-- MIGRAÇÃO: Alterar IDs de SERIAL (Long) para UUID
-- =============================================

-- =============================================
-- 1. Adicionar colunas UUID temporárias
-- =============================================
ALTER TABLE processo ADD COLUMN IF NOT EXISTS id_uuid UUID;
ALTER TABLE parte ADD COLUMN IF NOT EXISTS id_uuid UUID;
ALTER TABLE movimentacao ADD COLUMN IF NOT EXISTS id_uuid UUID;

-- =============================================
-- 2. Preencher as novas colunas com UUIDs
-- =============================================
UPDATE processo SET id_uuid = gen_random_uuid() WHERE id_uuid IS NULL;
UPDATE parte SET id_uuid = gen_random_uuid() WHERE id_uuid IS NULL;
UPDATE movimentacao SET id_uuid = gen_random_uuid() WHERE id_uuid IS NULL;

-- =============================================
-- 3. Atualizar chaves estrangeiras
-- =============================================
ALTER TABLE parte ADD COLUMN IF NOT EXISTS processo_id_uuid UUID;
ALTER TABLE movimentacao ADD COLUMN IF NOT EXISTS processo_id_uuid UUID;

UPDATE parte
SET processo_id_uuid = (SELECT id_uuid FROM processo WHERE processo.id = parte.processo_id)
WHERE processo_id_uuid IS NULL;

UPDATE movimentacao
SET processo_id_uuid = (SELECT id_uuid FROM processo WHERE processo.id = movimentacao.processo_id)
WHERE processo_id_uuid IS NULL;

-- =============================================
-- 4. Dropar constraints antigas
-- =============================================
ALTER TABLE parte DROP CONSTRAINT IF EXISTS parte_processo_id_fkey;
ALTER TABLE movimentacao DROP CONSTRAINT IF EXISTS movimentacao_processo_id_fkey;

-- =============================================
-- 5. Dropar colunas antigas e renomear novas
-- =============================================
ALTER TABLE processo DROP COLUMN IF EXISTS id;
ALTER TABLE processo RENAME COLUMN id_uuid TO id;

ALTER TABLE parte DROP COLUMN IF EXISTS id;
ALTER TABLE parte DROP COLUMN IF EXISTS processo_id;
ALTER TABLE parte RENAME COLUMN id_uuid TO id;
ALTER TABLE parte RENAME COLUMN processo_id_uuid TO processo_id;

ALTER TABLE movimentacao DROP COLUMN IF EXISTS id;
ALTER TABLE movimentacao DROP COLUMN IF EXISTS processo_id;
ALTER TABLE movimentacao RENAME COLUMN id_uuid TO id;
ALTER TABLE movimentacao RENAME COLUMN processo_id_uuid TO processo_id;

-- =============================================
-- 6. Adicionar PK e FKs novas
-- =============================================
ALTER TABLE processo ADD PRIMARY KEY (id);
ALTER TABLE parte ADD PRIMARY KEY (id);
ALTER TABLE movimentacao ADD PRIMARY KEY (id);

ALTER TABLE parte
    ADD CONSTRAINT fk_parte_processo
        FOREIGN KEY (processo_id) REFERENCES processo(id);

ALTER TABLE movimentacao
    ADD CONSTRAINT fk_movimentacao_processo
        FOREIGN KEY (processo_id) REFERENCES processo(id);

-- =============================================
-- 7. Remover sequências antigas (não mais necessárias com UUID)
-- =============================================
DROP SEQUENCE IF EXISTS processo_id_seq;
DROP SEQUENCE IF EXISTS parte_id_seq;
DROP SEQUENCE IF EXISTS movimentacao_id_seq;

-- =============================================
-- 8. Ajustes finais de performance e boas práticas
-- =============================================
CREATE INDEX IF NOT EXISTS idx_parte_processo_id ON parte(processo_id);
CREATE INDEX IF NOT EXISTS idx_movimentacao_processo_id ON movimentacao(processo_id);
CREATE INDEX IF NOT EXISTS idx_processo_numero ON processo(numero);

-- Verificação final
SELECT 'Migração UUID concluída com sucesso!' AS status;