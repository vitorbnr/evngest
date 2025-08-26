package com.vitorbnr.evngest.repository;

import com.vitorbnr.evngest.model.Inscricao;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {
    Optional<Inscricao> findByUsuarioAndEvento(Usuario usuario, Evento evento);
    List<Inscricao> findByEvento(Evento evento);
}
