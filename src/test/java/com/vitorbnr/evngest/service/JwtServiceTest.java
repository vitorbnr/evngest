package com.vitorbnr.evngest.service;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private final String secretKey = "exemplo-de-chave-secreta-super-longa-e-segura-para-testes-unitarios";
    private final String nomeDeUsuario = "usuarioTeste";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "EXPIRATION_TIME", 1000 * 60 * 60L);
    }

    @Test
    public void testGerarToken() {
        String token = jwtService.gerarToken(nomeDeUsuario);
        assertNotNull(token);
        assertEquals(nomeDeUsuario, jwtService.extrairNomeDeUsuario(token));
    }

    @Test
    public void testValidarToken() {
        String token = jwtService.gerarToken(nomeDeUsuario);
        assertTrue(jwtService.validarToken(token, nomeDeUsuario));
    }

    @Test
    public void testValidarTokenInvalido() {
        String token = jwtService.gerarToken(nomeDeUsuario);
        assertFalse(jwtService.validarToken(token, "outroUsuario"));
    }

    @Test
    public void testTokenExpirado() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "EXPIRATION_TIME", 1L);
        String token = jwtService.gerarToken(nomeDeUsuario);

        Thread.sleep(50);

        assertFalse(jwtService.validarToken(token, nomeDeUsuario));
    }

    @Test
    public void testExtrairNomeDeUsuarioDeTokenExpirado() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "EXPIRATION_TIME", 1L);
        String token = jwtService.gerarToken(nomeDeUsuario);
        Thread.sleep(50);
        assertEquals(nomeDeUsuario, jwtService.extrairNomeDeUsuario(token));
    }

    @Test
    public void testExtrairNomeDeUsuario() {
        String token = jwtService.gerarToken(nomeDeUsuario);
        assertEquals(nomeDeUsuario, jwtService.extrairNomeDeUsuario(token));
    }
}
