package com.vitorbnr.evngest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitorbnr.evngest.dto.LoginRequestDTO;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.UsuarioRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    void testRegistrarUsuarioComSucesso() throws Exception {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNomeDeUsuario("novo_usuario");
        novoUsuario.setSenha("senha123");
        novoUsuario.setEmail("novo@email.com");

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoUsuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDeUsuario").value("novo_usuario"));
    }

    @Test
    void testRegistrarUsuarioComNomeDeUsuarioDuplicado() throws Exception {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setNomeDeUsuario("usuario_existente");
        usuarioExistente.setSenha(passwordEncoder.encode("senhaForte"));
        usuarioExistente.setEmail("existente@email.com");
        usuarioRepository.save(usuarioExistente);

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNomeDeUsuario("usuario_existente");
        novoUsuario.setSenha("outraSenha");
        novoUsuario.setEmail("outro@email.com");

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoUsuario)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Nome de usuario ja esta em uso."));
    }

    @Test
    void testLoginUsuarioComSucesso() throws Exception {
        String senhaPlana = "senhaSegura123";
        Usuario usuario = new Usuario();
        usuario.setNomeDeUsuario("usuario_login");
        usuario.setSenha(passwordEncoder.encode(senhaPlana));
        usuario.setEmail("login@email.com");
        usuarioRepository.save(usuario);

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setNomeDeUsuario("usuario_login");
        loginRequest.setSenha(senhaPlana);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void testLoginUsuarioComSenhaInvalida() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNomeDeUsuario("usuario_senha_errada");
        usuario.setSenha(passwordEncoder.encode("senhaCorreta"));
        usuario.setEmail("senha@errada.com");
        usuarioRepository.save(usuario);

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setNomeDeUsuario("usuario_senha_errada");
        loginRequest.setSenha("senhaIncorreta");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Senha invalida."));
    }
}
