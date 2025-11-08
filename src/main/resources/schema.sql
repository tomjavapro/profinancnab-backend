
-- H2
-- create table if not exists transacao (
--     id serial primary key,
--     tipo int,
--     data date,
--     valor decimal,
--     cpf bigint,
--     cartao varchar(255),
--     hora time,
--     dono_loja varchar(255),
--     nome_loja varchar(255)
-- );

-- PostgreSQL
-- Script SQL incompatível - O schema.sql atual tem sintaxe H2
-- Configuração do PostgreSQL - Precisa de ajustes
-- Inicialização do banco - Modo "always" pode causar conflitos
-- Essas correções devem resolver o problema de criação do bean dataSourceScriptDatabaseInitializer e permitir o deploy com PostgreSQL.
CREATE TABLE IF NOT EXISTS transacao (
    id SERIAL PRIMARY KEY,
    tipo INTEGER,
    data DATE,
    valor DECIMAL(15,2),
    cpf BIGINT,
    cartao VARCHAR(255),
    hora TIME,
    dono_loja VARCHAR(255),
    nome_loja VARCHAR(255)
);