CREATE TABLE papel (
                       id UUID PRIMARY KEY,
                       nome VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE usuario_papeis (
                                usuario_id UUID NOT NULL,
                                papel_id UUID NOT NULL,
                                PRIMARY KEY (usuario_id, papel_id),
                                FOREIGN KEY (usuario_id) REFERENCES usuario(id),
                                FOREIGN KEY (papel_id) REFERENCES papel(id)
);

INSERT INTO papel (id, nome) VALUES (gen_random_uuid(), 'ADMIN');
INSERT INTO papel (id, nome) VALUES (gen_random_uuid(), 'USER');
