CREATE TABLE pauta (
    id UUID PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    inicio_votacao TIMESTAMP NULL,
    fim_votacao TIMESTAMP NULL
);