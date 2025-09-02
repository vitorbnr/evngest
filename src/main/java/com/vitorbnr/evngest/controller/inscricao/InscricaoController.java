package com.vitorbnr.evngest.controller.inscricao;

import com.vitorbnr.evngest.dto.EventoResponseDTO;
import com.vitorbnr.evngest.dto.InscricaoResponseDTO;
import com.vitorbnr.evngest.dto.UsuarioResponseDTO;
import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Inscricao;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.EventoRepository;
import com.vitorbnr.evngest.repository.InscricaoRepository;
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
@RequestMapping("/api/inscricao")
@Tag(name = "Inscrições", description = "Endpoints para gerenciar inscrições em eventos")
public class InscricaoController {

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private InscricaoResponseDTO toResponseDTO(Inscricao inscricao) {
        InscricaoResponseDTO dto = new InscricaoResponseDTO();
        dto.setId(inscricao.getId());
        dto.setDataInscricao(inscricao.getDataInscricao());

        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO();
        usuarioDTO.setId(inscricao.getUsuario().getId());
        usuarioDTO.setNomeDeUsuario(inscricao.getUsuario().getNomeDeUsuario());
        usuarioDTO.setEmail(inscricao.getUsuario().getEmail());
        dto.setUsuario(usuarioDTO);

        EventoResponseDTO eventoDTO = new EventoResponseDTO();
        eventoDTO.setId(inscricao.getEvento().getId());
        eventoDTO.setNome(inscricao.getEvento().getNome());
        dto.setEvento(eventoDTO);

        return dto;
    }

    @Operation(summary = "Inscreve o usuário autenticado em um evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscrição realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou Evento não encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuário já está inscrito neste evento")
    })
    @PostMapping("/{idEvento}")
    public ResponseEntity<?> inscreverEmEvento(@Parameter(description = "ID do evento para se inscrever") @PathVariable Long idEvento, Authentication authentication) {
        String nomeDeUsuario = authentication.getName();
        Usuario usuario = usuarioRepository.findByNomeDeUsuario(nomeDeUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado."));

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

    @Operation(summary = "Lista todos os inscritos em um evento específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inscrições retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<List<InscricaoResponseDTO>> listarInscritosPorEvento(@Parameter(description = "ID do evento para listar os inscritos") @PathVariable Long idEvento) {
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado."));

        List<InscricaoResponseDTO> inscritos = inscricaoRepository.findByEvento(evento)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(inscritos);
    }
}
