package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.config.JpaAuditingConfig;
import br.com.contasdomesticas.api.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario novoUsuario(String login) {
        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setNomeExibicao("Fulano de Tal");
        usuario.setSenha("hash-fake");
        return usuario;
    }

    @Test
    void devePersistirUsuarioComCamposDeAuditoriaPreenchidos() {
        Usuario salvo = usuarioRepository.save(novoUsuario("fulano"));

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getCriadoEm()).isNotNull();
        assertThat(salvo.getCriadoPor()).isEqualTo("sistema");
    }

    @Test
    void deveEncontrarPorLogin() {
        usuarioRepository.save(novoUsuario("ciclano"));

        Optional<Usuario> encontrado = usuarioRepository.findByLogin("ciclano");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNomeExibicao()).isEqualTo("Fulano de Tal");
        assertThat(usuarioRepository.existsByLogin("ciclano")).isTrue();
        assertThat(usuarioRepository.existsByLogin("inexistente")).isFalse();
    }
}
