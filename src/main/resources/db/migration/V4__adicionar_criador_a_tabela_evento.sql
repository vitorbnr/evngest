ALTER TABLE evento
    ADD COLUMN criador_id UUID NOT NULL;

ALTER TABLE evento
    ADD CONSTRAINT fk_evento_usuario
        FOREIGN KEY (criador_id)
            REFERENCES usuario(id);
