package com.vitorbnr.evngest.repository;

import com.vitorbnr.evngest.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByNomeDeUsuario(String nomeDeUsuario);
}
