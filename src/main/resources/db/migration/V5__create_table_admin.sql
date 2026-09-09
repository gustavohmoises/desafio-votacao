CREATE TABLE admin (
    id UUID PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    senha TEXT NOT NULL,
    data_cadastro TIMESTAMP NOT NULL
);