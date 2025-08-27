package com.vitorbnr.evngest.controller.avaliacao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitorbnr.evngest.model.Avaliacao;
import com.vitorbnr.evngest.model.Comentario;
import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.AvaliacaoRepository;
import com.vitorbnr.evngest.repository.ComentarioRepository;
import com.vitorbnr.evngest.repository.EventoRepository;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import com.vitorbnr.evngest.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AvaliacaoComentarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;
    private Evento evento;
    private String token;

    @BeforeEach
    void setUp() {
        comentarioRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        eventoRepository.deleteAll();
        usuarioRepository.deleteAll();

        usuario = new Usuario();
        usuario.setNomeDeUsuario("userTest");
        usuario.setEmail("test@test.com");
        usuario.setSenha(passwordEncoder.encode("password"));
        usuario = usuarioRepository.save(usuario);

        evento = new Evento();
        evento.setNome("Evento de Teste");
        evento.setDescricao("Descrição do Evento");
        evento.setLocalizacao("Local Teste");
        evento.setDataEvento(LocalDateTime.now().plusDays(5));
        evento.setCriador(usuario);
        evento = eventoRepository.save(evento);

        token = jwtService.gerarToken(usuario.getNomeDeUsuario());
    }

    @Test
    void testCriarEListarComentario() throws Exception {
        Comentario comentario = new Comentario();
        comentario.setTexto("Ótimo evento, muito bem organizado!");

        mockMvc.perform(post("/api/avaliacoes-comentarios/comentarios/" + evento.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comentario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.texto", is("Ótimo evento, muito bem organizado!")))
                .andExpect(jsonPath("$.usuario.nomeDeUsuario", is(usuario.getNomeDeUsuario())));

        mockMvc.perform(get("/api/avaliacoes-comentarios/comentarios/evento/" + evento.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].texto", is("Ótimo evento, muito bem organizado!")));
    }

    @Test
    void testCriarAvaliacaoComSucesso() throws Exception {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(5);

        mockMvc.perform(post("/api/avaliacoes-comentarios/avaliacoes/" + evento.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(avaliacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nota", is(5)))
                .andExpect(jsonPath("$.usuario.id", is(usuario.getId().toString())));
    }

    @Test
    void testCriarAvaliacaoDuplicada() throws Exception {
        Avaliacao primeiraAvaliacao = new Avaliacao();
        primeiraAvaliacao.setNota(4);
        primeiraAvaliacao.setUsuario(usuario);
        primeiraAvaliacao.setEvento(evento);
        avaliacaoRepository.save(primeiraAvaliacao);

        Avaliacao segundaAvaliacao = new Avaliacao();
        segundaAvaliacao.setNota(3);

        mockMvc.perform(post("/api/avaliacoes-comentarios/avaliacoes/" + evento.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segundaAvaliacao)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Usuario ja avaliou este evento."));
    }
}
