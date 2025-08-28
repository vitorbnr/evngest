package com.vitorbnr.evngest.controller.inscricao;

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

    @Operation(summary = "Inscreve o usuário autenticado em um evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscrição realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou Evento não encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuário já está inscrito neste evento")
    })
    @PostMapping("/{idEvento}")
    public ResponseEntity<?> inscreverEmEvento(@Parameter(description = "ID do evento para se inscrever") @PathVariable Long idEvento, Authentication authentication) {
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

    @Operation(summary = "Lista todos os inscritos em um evento específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inscrições retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<?> listarInscritosPorEvento(@Parameter(description = "ID do evento para listar os inscritos") @PathVariable Long idEvento) {
        Optional<Evento> eventoOpt = eventoRepository.findById(idEvento);

        if (eventoOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Evento nao encontrado.");
        }

        List<Inscricao> inscritos = inscricaoRepository.findByEvento(eventoOpt.get());

        return ResponseEntity.ok(inscritos);
    }
}
