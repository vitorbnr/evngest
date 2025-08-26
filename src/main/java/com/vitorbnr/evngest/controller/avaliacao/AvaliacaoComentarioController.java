package com.vitorbnr.evngest.controller.avaliacao;

import com.vitorbnr.evngest.model.Avaliacao;
import com.vitorbnr.evngest.model.Comentario;
import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.AvaliacaoRepository;
import com.vitorbnr.evngest.repository.ComentarioRepository;
import com.vitorbnr.evngest.repository.EventoRepository;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/avaliacoes-comentarios")
public class AvaliacaoComentarioController {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/comentarios/{idEvento}")
    public ResponseEntity<?> criarComentario(@PathVariable Long idEvento, @RequestBody Comentario comentario, Authentication authentication) {
        String nomeDeUsuario = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNomeDeUsuario(nomeDeUsuario);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario nao encontrado.");
        }
        Optional<Evento> eventoOpt = eventoRepository.findById(idEvento);
        if (eventoOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Evento nao encontrado.");
        }

        comentario.setUsuario(usuarioOpt.get());
        comentario.setEvento(eventoOpt.get());
        comentario.setDataComentario(LocalDateTime.now());

        Comentario novoComentario = comentarioRepository.save(comentario);
        return ResponseEntity.ok(novoComentario);
    }

    @GetMapping("/comentarios/evento/{idEvento}")
    public ResponseEntity<List<Comentario>> listarComentariosPorEvento(@PathVariable Long idEvento) {
        Optional<Evento> eventoOpt = eventoRepository.findById(idEvento);
        if (eventoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Comentario> comentarios = comentarioRepository.findByEvento(eventoOpt.get());
        return ResponseEntity.ok(comentarios);
    }

    @PostMapping("/avaliacoes/{idEvento}")
    public ResponseEntity<?> criarAvaliacao(@PathVariable Long idEvento, @RequestBody Avaliacao avaliacao, Authentication authentication) {
        String nomeDeUsuario = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNomeDeUsuario(nomeDeUsuario);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario nao encontrado.");
        }
        Optional<Evento> eventoOpt = eventoRepository.findById(idEvento);
        if (eventoOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Evento nao encontrado.");
        }

        Optional<Avaliacao> avaliacaoExistente = avaliacaoRepository.findByUsuarioAndEvento(usuarioOpt.get(), eventoOpt.get());
        if (avaliacaoExistente.isPresent()) {
            return ResponseEntity.status(409).body("Usuario ja avaliou este evento.");
        }

        avaliacao.setUsuario(usuarioOpt.get());
        avaliacao.setEvento(eventoOpt.get());

        Avaliacao novaAvaliacao = avaliacaoRepository.save(avaliacao);
        return ResponseEntity.ok(novaAvaliacao);
    }
}
