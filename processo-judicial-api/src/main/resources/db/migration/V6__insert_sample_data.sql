-- =============================================
-- V6__insert_sample_data.sql
-- Massa de dados para testes
-- =============================================

-- 1. Inserir Processos
INSERT INTO processo (id, numero, assunto, vara, status, data_abertura, criado_em, atualizado_em)
VALUES
    ('550e8400-e29b-41d4-a716-446655440001', '0001234-55.2026.8.26.0100', 'Execução Fiscal - IPTU', '3ª Vara da Fazenda Pública', 'ATIVO', '2026-05-10', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440002', '0005678-99.2026.8.26.0100', 'Cobrança Extrajudicial', '2ª Vara Cível', 'ATIVO', '2026-05-12', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440003', '0009999-11.2026.8.26.0100', 'Ação de Improbidade Administrativa', '1ª Vara da Fazenda Pública', 'SUSPENSO', '2026-04-20', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440004', '0012345-67.2026.8.26.0100', 'Execução Fiscal - ISS', '4ª Vara da Fazenda Pública', 'ENCERRADO', '2026-03-15', NOW(), NOW());

-- 2. Inserir Partes
INSERT INTO parte (id, processo_id, tipo, nome, documento, cep, logradouro, bairro, cidade, uf, criado_em)
VALUES
    -- Processo 1
    ('550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440001', 'AUTOR', 'Prefeitura Municipal de Taubaté', '12.345.678/0001-90', '12010100', 'Av. Dr. Pedro de Moraes', 'Centro', 'Taubaté', 'SP', NOW()),
    ('550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440001', 'REU', 'João da Silva Santos', '123.456.789-00', '12030145', 'Rua das Flores, 450', 'Jardim América', 'Taubaté', 'SP', NOW()),

    -- Processo 2
    ('550e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440002', 'AUTOR', 'Prefeitura Municipal de Taubaté', '12.345.678/0001-90', NULL, NULL, NULL, NULL, NULL, NOW()),
    ('550e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440002', 'REU', 'Maria Oliveira Souza', '987.654.321-00', '12020400', 'Av. Independência', 'Vila Ipiranga', 'Taubaté', 'SP', NOW()),

    -- Processo 3
    ('550e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440003', 'AUTOR', 'Prefeitura Municipal de Taubaté', '12.345.678/0001-90', NULL, NULL, NULL, NULL, NULL, NOW()),
    ('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440003', 'REU', 'Empresa XYZ Ltda', '12.345.678/0001-99', '12230000', 'Rodovia Presidente Dutra', 'Zona Industrial', 'Taubaté', 'SP', NOW());

-- 3. Inserir Movimentações
INSERT INTO movimentacao (id, processo_id, descricao, data_movimentacao)
VALUES
    -- Processo 1
    ('550e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440001', 'Petição inicial protocolada', NOW() - INTERVAL '12 days'),
    ('550e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440001', 'Citação realizada com sucesso', NOW() - INTERVAL '8 days'),
    ('550e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440001', 'Réu apresentou contestação', NOW() - INTERVAL '3 days'),

    -- Processo 2
    ('550e8400-e29b-41d4-a716-446655440014', '550e8400-e29b-41d4-a716-446655440002', 'Notificação extrajudicial enviada', NOW() - INTERVAL '10 days'),
    ('550e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440002', 'Acordo parcial celebrado', NOW() - INTERVAL '2 days'),

    -- Processo 3
    ('550e8400-e29b-41d4-a716-446655440016', '550e8400-e29b-41d4-a716-446655440003', 'Processo suspenso por decisão judicial', NOW() - INTERVAL '25 days');

-- Verificação final
SELECT '✅ Massa de dados inserida com sucesso!' AS mensagem;
SELECT 'Total de Processos:' as info, COUNT(*) FROM processo;