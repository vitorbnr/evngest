package com.vitorbnr.evngest.controller;

import com.vitorbnr.evngest.dto.LoginRequestDTO;
import com.vitorbnr.evngest.dto.UsuarioRequestDTO;
import com.vitorbnr.evngest.dto.UsuarioResponseDTO;
import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import com.vitorbnr.evngest.service.JwtService;
import com.vitorbnr.evngest.exception.RecursoNaoEncontradoException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Operation(summary = "Registra um novo usuário", description = "Cria um novo usuário no sistema com nome de usuário, e-mail e senha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nome de usuário já está em uso")
    })
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioRequestDTO usuarioDTO) {
        if (usuarioRepository.findByNomeDeUsuario(usuarioDTO.getNomeDeUsuario()).isPresent()) {
            return ResponseEntity.badRequest().body("Nome de usuario ja esta em uso.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNomeDeUsuario(usuarioDTO.getNomeDeUsuario());
        novoUsuario.setEmail(usuarioDTO.getEmail());
        novoUsuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(usuarioSalvo.getId());
        responseDTO.setNomeDeUsuario(usuarioSalvo.getNomeDeUsuario());
        responseDTO.setEmail(usuarioSalvo.getEmail());

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Realiza o login do usuário", description = "Autentica um usuário com nome de usuário e senha, e retorna um token JWT em caso de sucesso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido, retorna o token JWT"),
            @ApiResponse(responseCode = "400", description = "Senha inválida"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
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
