package com.vitorbnr.evngest.controller;

import com.vitorbnr.evngest.exception.RecursoNaoEncontradoException;
import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.EventoRepository;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import com.vitorbnr.evngest.service.notificacao.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@Tag(name = "Eventos", description = "Endpoints para gerenciar eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Operation(summary = "Lista todos os eventos disponíveis")
    @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso")
    @GetMapping
    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    @Operation(summary = "Cria um novo evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário criador não encontrado")
    })
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

    @Operation(summary = "Busca um evento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado com o ID fornecido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarEventoPorId(@Parameter(description = "ID do evento a ser buscado") @PathVariable Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento nao encontrado com o ID: " + id));
        return ResponseEntity.ok().body(evento);
    }

    @Operation(summary = "Atualiza um evento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para atualizar este evento"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado com o ID fornecido")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarEvento(@Parameter(description = "ID do evento a ser atualizado") @PathVariable Long id, @RequestBody Evento eventoDetalhes, Authentication authentication) {
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

    @Operation(summary = "Exclui um evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento excluído com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para excluir este evento"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado com o ID fornecido")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirEvento(@Parameter(description = "ID do evento a ser excluído") @PathVariable Long id, Authentication authentication) {
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
