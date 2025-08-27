package com.vitorbnr.evngest.controller.inscricao;

import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Inscricao;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.EventoRepository;
import com.vitorbnr.evngest.repository.InscricaoRepository;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import com.vitorbnr.evngest.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class InscricaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;
    private Evento evento;
    private String token;

    @BeforeEach
    void setUp() {
        inscricaoRepository.deleteAll();
        eventoRepository.deleteAll();
        usuarioRepository.deleteAll();

        usuario = new Usuario();
        usuario.setNomeDeUsuario("testuser");
        usuario.setSenha(passwordEncoder.encode("password"));
        usuario.setEmail("test@example.com");
        usuario = usuarioRepository.save(usuario);

        evento = new Evento();
        evento.setNome("Conferência de Tecnologia");
        evento.setCriador(usuario);
        evento.setDataEvento(LocalDateTime.now().plusDays(30));
        evento.setLocalizacao("Centro de Convenções");
        evento = eventoRepository.save(evento);

        token = jwtService.gerarToken(usuario.getNomeDeUsuario());
    }

    @Test
    void testInscreverEmEventoComSucesso() throws Exception {
        mockMvc.perform(post("/api/inscricao/" + evento.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Inscricao realizada com sucesso."));
    }

    @Test
    void testInscreverEmEventoJaInscrito() throws Exception {
        Inscricao inscricao = new Inscricao();
        inscricao.setUsuario(usuario);
        inscricao.setEvento(evento);
        inscricao.setDataInscricao(LocalDateTime.now());
        inscricaoRepository.save(inscricao);

        mockMvc.perform(post("/api/inscricao/" + evento.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(content().string("Usuario ja esta inscrito neste evento."));
    }

    @Test
    void testInscreverEmEventoInexistente() throws Exception {
        long idEventoInexistente = 999L;
        mockMvc.perform(post("/api/inscricao/" + idEventoInexistente)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Evento nao encontrado."));
    }

    @Test
    void testListarInscritosPorEvento() throws Exception {
        Inscricao inscricao = new Inscricao();
        inscricao.setUsuario(usuario);
        inscricao.setEvento(evento);
        inscricao.setDataInscricao(LocalDateTime.now());
        inscricaoRepository.save(inscricao);

        mockMvc.perform(get("/api/inscricao/evento/" + evento.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].usuario.nomeDeUsuario").value(usuario.getNomeDeUsuario()));
    }
}
