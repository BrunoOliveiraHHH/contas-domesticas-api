package br.com.contasdomesticas.api.config.security.userDetail;

import br.com.contasdomesticas.api.domain.Usuario;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public AuthenticationUser loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByLogin(username)
            .orElseThrow(() -> new AplicacaoException("Usuário não encontrado", HttpStatus.UNAUTHORIZED));

        return new AuthenticationUser(usuario);
    }
}
