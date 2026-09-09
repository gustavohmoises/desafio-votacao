CREATE TABLE voto_invalido (
    id UUID PRIMARY KEY,
    pauta_id UUID,
    associado_id UUID,
    voto VARCHAR(3),
    data_envio TIMESTAMP NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    mensagem VARCHAR(255) NOT NULL
);