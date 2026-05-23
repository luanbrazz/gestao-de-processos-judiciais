-- =============================================
-- Aumentar tamanho do campo numero para suportar formato CNJ completo
-- =============================================

ALTER TABLE processo
ALTER COLUMN numero TYPE VARCHAR(50);

DROP INDEX IF EXISTS idx_processo_numero;
CREATE INDEX idx_processo_numero ON processo USING btree (numero);

SELECT
    column_name,
    data_type,
    character_maximum_length
FROM information_schema.columns
WHERE table_name = 'processo' AND column_name = 'numero';