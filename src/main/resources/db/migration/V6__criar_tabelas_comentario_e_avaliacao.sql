CREATE TABLE comentario(
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      UUID      NOT NULL,
    evento_id       BIGINT    NOT NULL,
    texto           TEXT      NOT NULL,
    data_comentario TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    FOREIGN KEY (evento_id) REFERENCES evento (id)
);

CREATE TABLE avaliacao(
    id         BIGSERIAL PRIMARY KEY,
    usuario_id UUID    NOT NULL,
    evento_id  BIGINT  NOT NULL,
    nota       INTEGER NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    FOREIGN KEY (evento_id) REFERENCES evento (id),
    UNIQUE (usuario_id, evento_id)
);
