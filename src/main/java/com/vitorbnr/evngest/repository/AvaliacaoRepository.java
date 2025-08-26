package com.vitorbnr.evngest.repository;

import com.vitorbnr.evngest.model.Avaliacao;
import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Optional<Avaliacao> findByUsuarioAndEvento(Usuario usuario, Evento evento);
    List<Avaliacao> findByEvento(Evento evento);
}
