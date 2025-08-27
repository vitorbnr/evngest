package com.vitorbnr.evngest.controller;

import com.vitorbnr.evngest.dto.LoginRequestDTO;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import com.vitorbnr.evngest.service.JwtService;
import com.vitorbnr.evngest.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByNomeDeUsuario(usuario.getNomeDeUsuario()).isPresent()) {
            return ResponseEntity.badRequest().body("Nome de usuario ja esta em uso.");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario novoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequestDTO loginRequest) {
        Usuario usuario = usuarioRepository.findByNomeDeUsuario(loginRequest.getNomeDeUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado."));

        if (passwordEncoder.matches(loginRequest.getSenha(), usuario.getSenha())) {
            String token = jwtService.gerarToken(usuario.getNomeDeUsuario());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body("Senha invalida.");
        }
    }
}
