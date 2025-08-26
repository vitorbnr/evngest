package com.vitorbnr.evngest.controller.inscricao;

import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Inscricao;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.EventoRepository;
import com.vitorbnr.evngest.repository.InscricaoRepository;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inscricao")
public class InscricaoController {

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/{idEvento}")
    public ResponseEntity<?> inscreverEmEvento(@PathVariable Long idEvento, Authentication authentication) {
        String nomeDeUsuario = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNomeDeUsuario(nomeDeUsuario);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario nao encontrado.");
        }

        Optional<Evento> eventoOpt = eventoRepository.findById(idEvento);
        if (eventoOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Evento nao encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        Evento evento = eventoOpt.get();

        Optional<Inscricao> inscricaoExistente = inscricaoRepository.findByUsuarioAndEvento(usuario, evento);
        if (inscricaoExistente.isPresent()) {
            return ResponseEntity.status(409).body("Usuario ja esta inscrito neste evento.");
        }

        Inscricao novaInscricao = new Inscricao();
        novaInscricao.setUsuario(usuario);
        novaInscricao.setEvento(evento);
        novaInscricao.setDataInscricao(LocalDateTime.now());

        inscricaoRepository.save(novaInscricao);

        return ResponseEntity.ok("Inscricao realizada com sucesso.");
    }

    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<?> listarInscritosPorEvento(@PathVariable Long idEvento) {
        Optional<Evento> eventoOpt = eventoRepository.findById(idEvento);

        if (eventoOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Evento nao encontrado.");
        }

        List<Inscricao> inscritos = inscricaoRepository.findByEvento(eventoOpt.get());

        return ResponseEntity.ok(inscritos);
    }
}
