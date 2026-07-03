package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Usuario;
import br.com.contasdomesticas.api.dto.UsuarioRequest;
import br.com.contasdomesticas.api.dto.UsuarioResponse;
import br.com.contasdomesticas.api.exception.LoginJaExisteException;
import br.com.contasdomesticas.api.exception.RecursoNaoEncontradoException;
import br.com.contasdomesticas.api.mapper.UsuarioMapper;
import br.com.contasdomesticas.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return usuarioMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        if (usuarioRepository.existsByLogin(request.login())) {
            throw new LoginJaExisteException(request.login());
        }
        Usuario usuario = new Usuario();
        usuario.setLogin(request.login());
        usuario.setNomeExibicao(request.nomeExibicao());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);
        if (!usuario.getLogin().equals(request.login())
                && usuarioRepository.existsByLogin(request.login())) {
            throw new LoginJaExisteException(request.login());
        }
        usuario.setLogin(request.login());
        usuario.setNomeExibicao(request.nomeExibicao());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void remover(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuarioRepository.delete(usuario);
    }

    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuario nao encontrado: " + id));
    }
}
