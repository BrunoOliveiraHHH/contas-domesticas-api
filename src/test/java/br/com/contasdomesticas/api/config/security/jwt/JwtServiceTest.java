package br.com.contasdomesticas.api.config.security.jwt;

import br.com.contasdomesticas.api.config.security.userDetail.AuthenticationUser;
import br.com.contasdomesticas.api.domain.Usuario;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private JwtService jwtService;
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        jwtService = new JwtService(usuarioRepository);
        ReflectionTestUtils.setField(jwtService, "secret",
            "chave-de-teste-suficientemente-grande-para-hs256-0123456789abcdef");
        ReflectionTestUtils.setField(jwtService, "accessExpiration", Duration.ofMinutes(15));
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", Duration.ofDays(7));
        ReflectionTestUtils.invokeMethod(jwtService, "init");
    }

    @Test
    void deveGerarEValidarTokenExtraindoOLogin() {
        String token = jwtService.generateToken("admin");

        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin");
    }

    @Test
    void deveRejeitarTokenAdulterado() {
        String token = jwtService.generateToken("admin");
        String adulterado = token.substring(0, token.length() - 3) + "abc";

        assertThatThrownBy(() -> jwtService.extractUsername(adulterado))
            .isInstanceOf(AplicacaoException.class);
    }

    @Test
    void deveResolverUsuarioAPartirDeTokenValido() {
        Usuario usuario = new Usuario();
        usuario.setLogin("admin");
        usuario.setSenha("hash");
        usuario.setNomeExibicao("Admin");
        when(usuarioRepository.findByLogin("admin")).thenReturn(Optional.of(usuario));

        String token = jwtService.generateToken("admin");
        AuthenticationUser user = jwtService.validateTokenAndGetUser(token);

        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getNomeExibicao()).isEqualTo("Admin");
    }

    @Test
    void deveRejeitarTokenAusenteOuVazio() {
        assertThatThrownBy(() -> jwtService.validateTokenAndGetUser("   "))
            .isInstanceOf(AplicacaoException.class);
    }
}
