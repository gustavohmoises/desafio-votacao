CREATE TABLE voto (
    id UUID PRIMARY KEY,
    pauta_id UUID NOT NULL,
    associado_id UUID NOT NULL,
    voto VARCHAR(3) NOT NULL,
    data_envio TIMESTAMP NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,

    CONSTRAINT fk_voto_pauta
        FOREIGN KEY (pauta_id)
        REFERENCES pauta(id),

    CONSTRAINT fk_voto_associado
        FOREIGN KEY (associado_id)
        REFERENCES associado(id),

    CONSTRAINT uk_voto_associado_pauta
        UNIQUE (pauta_id, associado_id),

    CONSTRAINT ck_voto
        CHECK (voto IN ('SIM', 'NAO'))
);