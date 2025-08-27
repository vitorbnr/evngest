package com.vitorbnr.evngest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.EventoRepository;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import com.vitorbnr.evngest.service.JwtService;
import com.vitorbnr.evngest.service.notificacao.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class EventoControllerIntegrationTest {

    @MockBean
    private NotificacaoService notificacaoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;
    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        eventoRepository.deleteAll();
        usuarioRepository.deleteAll();

        usuario = new Usuario();
        usuario.setNomeDeUsuario("testuser");
        usuario.setSenha(passwordEncoder.encode("password"));
        usuario.setEmail("test@example.com");
        usuario = usuarioRepository.save(usuario);

        token = jwtService.gerarToken(usuario.getNomeDeUsuario());
    }

    @Test
    public void testCriarListarEBuscarEvento() throws Exception {
        Evento evento = new Evento();
        evento.setNome("Festival de Verão");
        evento.setDescricao("Um grande festival de música.");
        evento.setLocalizacao("Praia da Costa");
        evento.setDataEvento(LocalDateTime.now().plusDays(10));

        String responseContent = mockMvc.perform(post("/api/eventos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Festival de Verão"))
                .andReturn().getResponse().getContentAsString();

        Evento eventoCriado = objectMapper.readValue(responseContent, Evento.class);
        Long eventoId = eventoCriado.getId();

        mockMvc.perform(get("/api/eventos/" + eventoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventoId))
                .andExpect(jsonPath("$.nome").value("Festival de Verão"));

        mockMvc.perform(get("/api/eventos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Festival de Verão"));
    }

    @Test
    public void testAtualizarEvento() throws Exception {
        Evento evento = new Evento();
        evento.setNome("Evento Original");
        evento.setCriador(usuario);
        evento.setDataEvento(LocalDateTime.now());
        evento.setLocalizacao("Local Original");
        evento = eventoRepository.save(evento);

        Evento eventoDetalhes = new Evento();
        eventoDetalhes.setNome("Evento Atualizado com Sucesso");
        eventoDetalhes.setDescricao("Descrição atualizada.");
        eventoDetalhes.setLocalizacao("Novo Local");
        eventoDetalhes.setDataEvento(LocalDateTime.now().plusHours(1));

        mockMvc.perform(put("/api/eventos/" + evento.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventoDetalhes)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Evento Atualizado com Sucesso"))
                .andExpect(jsonPath("$.descricao").value("Descrição atualizada."));
    }

    @Test
    public void testExcluirEvento() throws Exception {
        Evento evento = new Evento();
        evento.setNome("Evento a ser Excluído");
        evento.setCriador(usuario);
        evento.setDataEvento(LocalDateTime.now());
        evento.setLocalizacao("Qualquer Lugar");
        evento = eventoRepository.save(evento);

        mockMvc.perform(delete("/api/eventos/" + evento.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/eventos/" + evento.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAtualizarEventoNaoAutorizado() throws Exception {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setNomeDeUsuario("outroUser");
        outroUsuario.setSenha(passwordEncoder.encode("password"));
        outroUsuario.setEmail("outro@example.com");
        usuarioRepository.save(outroUsuario);
        String outroToken = jwtService.gerarToken(outroUsuario.getNomeDeUsuario());

        Evento evento = new Evento();
        evento.setNome("Evento do TestUser");
        evento.setCriador(usuario);
        evento.setDataEvento(LocalDateTime.now());
        evento.setLocalizacao("Local");
        evento = eventoRepository.save(evento);

        Evento eventoDetalhes = new Evento();
        eventoDetalhes.setNome("Tentativa de Update");

        mockMvc.perform(put("/api/eventos/" + evento.getId())
                        .header("Authorization", "Bearer " + outroToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventoDetalhes)))
                .andExpect(status().isForbidden());
    }
}
