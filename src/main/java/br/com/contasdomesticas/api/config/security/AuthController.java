package br.com.contasdomesticas.api.config.security;

import br.com.contasdomesticas.api.config.security.jwt.JwtService;
import br.com.contasdomesticas.api.config.security.userDetail.AuthenticationService;
import br.com.contasdomesticas.api.config.security.userDetail.AuthenticationUser;
import br.com.contasdomesticas.api.dto.LoginRequest;
import br.com.contasdomesticas.api.dto.RefreshRequest;
import br.com.contasdomesticas.api.dto.TokenResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Autenticacao: login (credenciais -> tokens), refresh e logout.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest request) {
        AuthenticationUser usuario = authenticationService.loadUserByUsername(request.login());
        if (!passwordEncoder.matches(request.senha(), usuario.getPassword())) {
            // Mensagem generica: nao revela se o login existe.
            throw new AplicacaoException("Credenciais invalidas", HttpStatus.UNAUTHORIZED);
        }
        return tokensPara(usuario.getUsername());
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody @Valid RefreshRequest request) {
        AuthenticationUser usuario = jwtService.validateTokenAndGetUser(request.refreshToken());
        return tokensPara(usuario.getUsername());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
        // Stateless: o cliente descarta os tokens. (denylist de refresh e opcional/futuro)
    }

    private TokenResponse tokensPara(String login) {
        return new TokenResponse(
            jwtService.generateToken(login),
            jwtService.generateRefreshToken(login),
            "Bearer",
            jwtService.getAccessExpirationSeconds());
    }
}
