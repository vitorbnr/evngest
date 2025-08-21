CREATE TABLE evento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_evento TIMESTAMP NOT NULL,
    localizacao VARCHAR(255) NOT NULL
);
