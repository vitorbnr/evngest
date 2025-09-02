package com.vitorbnr.evngest.controller;

import com.vitorbnr.evngest.dto.EventoRequestDTO;
import com.vitorbnr.evngest.dto.EventoResponseDTO;
import com.vitorbnr.evngest.dto.UsuarioResponseDTO;
import com.vitorbnr.evngest.exception.RecursoNaoEncontradoException;
import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.EventoRepository;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import com.vitorbnr.evngest.service.notificacao.NotificacaoService;
import com.vitorbnr.evngest.service.relatorio.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private RelatorioService relatorioService;

    private EventoResponseDTO toResponseDTO(Evento evento) {
        EventoResponseDTO dto = new EventoResponseDTO();
        dto.setId(evento.getId());
        dto.setNome(evento.getNome());
        dto.setDescricao(evento.getDescricao());
        dto.setDataEvento(evento.getDataEvento());
        dto.setLocalizacao(evento.getLocalizacao());

        UsuarioResponseDTO criadorDTO = new UsuarioResponseDTO();
        criadorDTO.setId(evento.getCriador().getId());
        criadorDTO.setNomeDeUsuario(evento.getCriador().getNomeDeUsuario());
        criadorDTO.setEmail(evento.getCriador().getEmail());
        dto.setCriador(criadorDTO);

        return dto;
    }

    @Operation(summary = "Lista todos os eventos disponíveis")
    @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso")
    @GetMapping
    public List<EventoResponseDTO> listarEventos() {
        return eventoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Cria um novo evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário criador não encontrado")
    })
    @PostMapping
    public ResponseEntity<EventoResponseDTO> criarEvento(@RequestBody EventoRequestDTO eventoDTO, Authentication authentication) {
        String nomeDeUsuario = authentication.getName();
        Usuario usuario = usuarioRepository.findByNomeDeUsuario(nomeDeUsuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado."));

        Evento novoEvento = new Evento();
        novoEvento.setNome(eventoDTO.getNome());
        novoEvento.setDescricao(eventoDTO.getDescricao());
        novoEvento.setDataEvento(eventoDTO.getDataEvento());
        novoEvento.setLocalizacao(eventoDTO.getLocalizacao());
        novoEvento.setCriador(usuario);

        Evento eventoSalvo = eventoRepository.save(novoEvento);
        notificacaoService.enviarNotificacao("Novo evento criado: " + eventoSalvo.getNome());

        return ResponseEntity.ok(toResponseDTO(eventoSalvo));
    }

    @Operation(summary = "Busca um evento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado com o ID fornecido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscarEventoPorId(@Parameter(description = "ID do evento a ser buscado") @PathVariable Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento nao encontrado com o ID: " + id));
        return ResponseEntity.ok().body(toResponseDTO(evento));
    }

    @Operation(summary = "Atualiza um evento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para atualizar este evento"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado com o ID fornecido")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarEvento(@Parameter(description = "ID do evento a ser atualizado") @PathVariable Long id, @RequestBody EventoRequestDTO eventoDetalhes, Authentication authentication) {
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
        return ResponseEntity.ok(toResponseDTO(eventoAtualizado));
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

    @Operation(summary = "Exporta a lista de inscritos de um evento em CSV")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para gerar este relatório"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @GetMapping("/{id}/inscritos/exportar-csv")
    public ResponseEntity<String> exportarInscritosCsv(
            @Parameter(description = "ID do evento para exportar os inscritos") @PathVariable Long id,
            Authentication authentication) {

        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado com o ID: " + id));

        if (!evento.getCriador().getNomeDeUsuario().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        String csvContent = relatorioService.gerarRelatorioInscritosCsv(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inscritos_evento_" + id + ".csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }
}

