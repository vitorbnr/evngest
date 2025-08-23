CREATE TABLE inscricao (
    id BIGSERIAL PRIMARY KEY,
    usuario_id UUID NOT NULL,
    evento_id BIGINT NOT NULL,
    data_inscricao TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (evento_id) REFERENCES evento(id),
    UNIQUE (usuario_id, evento_id)
);
