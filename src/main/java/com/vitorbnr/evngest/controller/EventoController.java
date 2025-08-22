package com.vitorbnr.evngest.controller;

import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.EventoRepository;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> criarEvento(@RequestBody Evento evento, Authentication authentication) {
        String nomeDeUsuario = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNomeDeUsuario(nomeDeUsuario);

        if (usuarioOpt.isPresent()) {
            evento.setCriador(usuarioOpt.get());
            Evento novoEvento = eventoRepository.save(evento);
            return ResponseEntity.ok(novoEvento);
        } else {
            return ResponseEntity.status(404).body("Usuario nao encontrado.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarEventoPorId(@PathVariable Long id) {
        return eventoRepository.findById(id)
                .map(evento -> ResponseEntity.ok().body(evento))
                .orElse(ResponseEntity.notFound().build());
    }
}
