    package com.vitorbnr.evngest.controller.avaliacao;

import com.vitorbnr.evngest.dto.*;
import com.vitorbnr.evngest.exception.RecursoNaoEncontradoException;
import com.vitorbnr.evngest.model.Avaliacao;
import com.vitorbnr.evngest.model.Comentario;
import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.AvaliacaoRepository;
import com.vitorbnr.evngest.repository.ComentarioRepository;
import com.vitorbnr.evngest.repository.EventoRepository;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/avaliacoes-comentarios")
@Tag(name = "Avaliações e Comentários", description = "Endpoints para gerenciar avaliações e comentários de eventos")
public class AvaliacaoComentarioController {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioResponseDTO toUsuarioResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNomeDeUsuario(usuario.getNomeDeUsuario());
        dto.setEmail(usuario.getEmail());
        return dto;
    }

    private ComentarioResponseDTO toComentarioResponseDTO(Comentario comentario) {
        ComentarioResponseDTO dto = new ComentarioResponseDTO();
        dto.setId(comentario.getId());
        dto.setTexto(comentario.getTexto());
        dto.setDataComentario(comentario.getDataComentario());
        dto.setUsuario(toUsuarioResponseDTO(comentario.getUsuario()));
        return dto;
    }

    private AvaliacaoResponseDTO toAvaliacaoResponseDTO(Avaliacao avaliacao) {
        AvaliacaoResponseDTO dto = new AvaliacaoResponseDTO();
        dto.setId(avaliacao.getId());
        dto.setNota(avaliacao.getNota());
        dto.setUsuario(toUsuarioResponseDTO(avaliacao.getUsuario()));
        return dto;
    }

    @Operation(summary = "Cria um novo comentário para um evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentário criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou Evento não encontrado")
    })
    @PostMapping("/comentarios/{idEvento}")
    public ResponseEntity<ComentarioResponseDTO> criarComentario(@Parameter(description = "ID do evento a ser comentado") @PathVariable Long idEvento, @RequestBody ComentarioRequestDTO comentarioDTO, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByNomeDeUsuario(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado."));

        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento nao encontrado."));

        Comentario comentario = new Comentario();
        comentario.setTexto(comentarioDTO.getTexto());
        comentario.setUsuario(usuario);
        comentario.setEvento(evento);
        comentario.setDataComentario(LocalDateTime.now());

        Comentario novoComentario = comentarioRepository.save(comentario);
        return ResponseEntity.ok(toComentarioResponseDTO(novoComentario));
    }

    @Operation(summary = "Lista todos os comentários de um evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentários listados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @GetMapping("/comentarios/evento/{idEvento}")
    public ResponseEntity<List<ComentarioResponseDTO>> listarComentariosPorEvento(@Parameter(description = "ID do evento para listar os comentários") @PathVariable Long idEvento) {
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento nao encontrado."));

        List<ComentarioResponseDTO> comentarios = comentarioRepository.findByEvento(evento).stream()
                .map(this::toComentarioResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(comentarios);
    }

    @Operation(summary = "Cria uma nova avaliação (nota) para um evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avaliação criada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou Evento não encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuário já avaliou este evento")
    })
    @PostMapping("/avaliacoes/{idEvento}")
    public ResponseEntity<?> criarAvaliacao(@Parameter(description = "ID do evento a ser avaliado") @PathVariable Long idEvento, @RequestBody AvaliacaoRequestDTO avaliacaoDTO, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByNomeDeUsuario(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado."));

        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento nao encontrado."));

        Optional<Avaliacao> avaliacaoExistente = avaliacaoRepository.findByUsuarioAndEvento(usuario, evento);
        if (avaliacaoExistente.isPresent()) {
            return ResponseEntity.status(409).body("Usuario ja avaliou este evento.");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(avaliacaoDTO.getNota());
        avaliacao.setUsuario(usuario);
        avaliacao.setEvento(evento);

        Avaliacao novaAvaliacao = avaliacaoRepository.save(avaliacao);
        return ResponseEntity.ok(toAvaliacaoResponseDTO(novaAvaliacao));
    }
}
