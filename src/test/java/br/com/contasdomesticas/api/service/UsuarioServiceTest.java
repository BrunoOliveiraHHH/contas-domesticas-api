package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Usuario;
import br.com.contasdomesticas.api.dto.UsuarioRequest;
import br.com.contasdomesticas.api.dto.UsuarioResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.UsuarioMapper;
import br.com.contasdomesticas.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCriarUsuarioCodificandoASenha() {
        UsuarioRequest request = new UsuarioRequest("joao", "Joao Silva", "senha123");
        when(usuarioRepository.existsByLogin("joao")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioMapper.toResponse(any(Usuario.class)))
                .thenReturn(new UsuarioResponse(1L, "joao", "Joao Silva", null, null, null, null));

        UsuarioResponse response = usuarioService.criar(request);

        assertThat(response.login()).isEqualTo("joao");
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getSenha())
                .isEqualTo("$2a$hash")
                .isNotEqualTo("senha123");
    }

    @Test
    void naoDeveCriarUsuarioComLoginDuplicado() {
        UsuarioRequest request = new UsuarioRequest("joao", "Joao Silva", "senha123");
        when(usuarioRepository.existsByLogin("joao")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.criar(request))
                .isInstanceOf(AplicacaoException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(AplicacaoException.class);
    }
}
