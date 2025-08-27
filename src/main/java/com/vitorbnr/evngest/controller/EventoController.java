package com.vitorbnr.evngest.controller;

import com.vitorbnr.evngest.exception.RecursoNaoEncontradoException;
import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.EventoRepository;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import com.vitorbnr.evngest.service.notificacao.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping
    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> criarEvento(@RequestBody Evento evento, Authentication authentication) {
        String nomeDeUsuario = authentication.getName();
        Usuario usuario = usuarioRepository.findByNomeDeUsuario(nomeDeUsuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado."));

        evento.setCriador(usuario);
        Evento novoEvento = eventoRepository.save(evento);
        notificacaoService.enviarNotificacao("Novo evento criado: " + novoEvento.getNome());
        return ResponseEntity.ok(novoEvento);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarEventoPorId(@PathVariable Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento nao encontrado com o ID: " + id));
        return ResponseEntity.ok().body(evento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarEvento(@PathVariable Long id, @RequestBody Evento eventoDetalhes, Authentication authentication) {
        Evento eventoExistente = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento nao encontrado com o ID: " + id));

        String nomeDeUsuario = authentication.getName();
        if (!eventoExistente.getCriador().getNomeDeUsuario().equals(nomeDeUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Voce nao tem permissao para atualizar este evento.");
        }

        eventoExistente.setNome(eventoDetalhes.getNome());
        eventoExistente.setDescricao(eventoDetalhes.getDescricao());
        eventoExistente.setLocalizacao(eventoDetalhes.getLocalizacao());
        eventoExistente.setDataEvento(eventoDetalhes.getDataEvento());

        Evento eventoAtualizado = eventoRepository.save(eventoExistente);
        return ResponseEntity.ok(eventoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirEvento(@PathVariable Long id, Authentication authentication) {
        Evento eventoExistente = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento nao encontrado com o ID: " + id));

        String nomeDeUsuario = authentication.getName();
        if (!eventoExistente.getCriador().getNomeDeUsuario().equals(nomeDeUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Voce nao tem permissao para excluir este evento.");
        }
        eventoRepository.delete(eventoExistente);
        return ResponseEntity.noContent().build();
    }
}
