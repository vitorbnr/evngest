package com.vitorbnr.evngest.repository;

import com.vitorbnr.evngest.model.Comentario;
import com.vitorbnr.evngest.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByEvento(Evento evento);
}
