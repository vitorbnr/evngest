package com.vitorbnr.evngest.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String gerarToken(String nomeDeUsuario) {
        return Jwts.builder()
                .setSubject(nomeDeUsuario)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validarToken(String token, String nomeDeUsuario) {
        final String nomeDeUsuarioDoToken = extrairNomeDeUsuario(token);
        return (nomeDeUsuarioDoToken != null && nomeDeUsuarioDoToken.equals(nomeDeUsuario) && !isTokenExpirado(token));
    }

    public String extrairNomeDeUsuario(String token) {
        try {
            return extrairClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    private Date extrairDataExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpirado(String token) {
        try {
            return extrairDataExpiracao(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Claims extrairTodasAsClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | IllegalArgumentException e) {
            throw new RuntimeException("Token invalido ou expirado.", e);
        }
    }

    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extrairTodasAsClaims(token);
        return claimsResolver.apply(claims);
    }
}
