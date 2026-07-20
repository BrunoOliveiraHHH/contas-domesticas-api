package br.com.contasdomesticas.api.config.security;

import br.com.contasdomesticas.api.config.security.userDetail.AuthenticationService;
import br.com.contasdomesticas.api.config.security.userDetail.AuthenticationUser;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationProviderManager implements AuthenticationProvider {

    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String usuario = authentication.getName();
        String senha = authentication.getCredentials() != null
            ? authentication.getCredentials().toString() : null;

        if (senha == null) {
            throw new AplicacaoException("Credenciais nao informadas", HttpStatus.UNAUTHORIZED);
        }

        AuthenticationUser authenticationUser = authenticationService.loadUserByUsername(usuario);

        if (!passwordEncoder.matches(senha, authenticationUser.getPassword())) {
            throw new AplicacaoException("Credenciais invalidas", HttpStatus.UNAUTHORIZED);
        }

        // Autenticado: nao expoe a senha nas credenciais do token.
        return new UsernamePasswordAuthenticationToken(
            authenticationUser, null, authenticationUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
