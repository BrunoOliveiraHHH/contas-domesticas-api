package br.com.contasdomesticas.api.config.security.jwt;

import br.com.contasdomesticas.api.config.security.userDetail.AuthenticationUser;
import br.com.contasdomesticas.api.domain.Usuario;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

/**
 * Emissao e validacao de JWT (HS256). Segredo e expiracoes vem de
 * {@code app.security.jwt.*} (ver application.yml).
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final UsuarioRepository usuarioRepository;

    @Value("${app.security.jwt.secret}")
    private String secret;
    @Value("${app.security.jwt.access-expiration}")
    private Duration accessExpiration;
    @Value("${app.security.jwt.refresh-expiration}")
    private Duration refreshExpiration;

    private SecretKey secretKey;

    @PostConstruct
    void init() {
        // O segredo e uma string (nao Base64); usamos os bytes UTF-8 diretamente.
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return build(username, accessExpiration);
    }

    public String generateRefreshToken(String username) {
        return build(username, refreshExpiration);
    }

    public long getAccessExpirationSeconds() {
        return accessExpiration.toSeconds();
    }

    private String build(String username, Duration ttl) {
        Date now = new Date();
        return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + ttl.toMillis()))
            .signWith(secretKey)
            .compact();
    }

    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (JwtException e) {
            throw new AplicacaoException("Token invalido", HttpStatus.UNAUTHORIZED);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (ExpiredJwtException e) {
            throw new AplicacaoException("Token expirado", HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            throw new AplicacaoException("Token invalido", HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional(readOnly = true)
    public AuthenticationUser validateTokenAndGetUser(String token) {
        if (token == null || token.isBlank()) {
            throw new AplicacaoException("Token ausente ou vazio", HttpStatus.UNAUTHORIZED);
        }
        if (!isTokenValid(token)) {
            throw new AplicacaoException("Token invalido", HttpStatus.UNAUTHORIZED);
        }
        String username = extractUsername(token);
        Usuario usuario = usuarioRepository.findByLogin(username)
            .orElseThrow(() -> new AplicacaoException("Usuario nao encontrado", HttpStatus.UNAUTHORIZED));
        return new AuthenticationUser(usuario);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
}
