package com.vitorbnr.evngest.config.security;

import com.vitorbnr.evngest.model.Usuario;
import com.vitorbnr.evngest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByNomeDeUsuario(username)
                .map(this::criarUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + username));
    }

    private UserDetails criarUserDetails(Usuario usuario) {
        Collection<GrantedAuthority> authorities = usuario.getPapeis().stream()
                .map(papel -> new SimpleGrantedAuthority("ROLE_" + papel.getNome()))
                .collect(Collectors.toList());

        return User.withUsername(usuario.getNomeDeUsuario())
                .password(usuario.getSenha())
                .authorities(authorities)
                .build();
    }
}
