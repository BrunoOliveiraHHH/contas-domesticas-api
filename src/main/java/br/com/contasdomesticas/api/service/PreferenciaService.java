package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Preferencia;
import br.com.contasdomesticas.api.domain.Usuario;
import br.com.contasdomesticas.api.dto.PreferenciaRequest;
import br.com.contasdomesticas.api.dto.PreferenciaResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.PreferenciaMapper;
import br.com.contasdomesticas.api.repository.PreferenciaRepository;
import br.com.contasdomesticas.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PreferenciaService {

    private final PreferenciaRepository preferenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PreferenciaMapper preferenciaMapper;

    /** Resolve a preferencia: valor do usuario -> valor global -> 404. */
    @Transactional(readOnly = true)
    public PreferenciaResponse resolver(String chave, Long usuarioId) {
        if (usuarioId != null) {
            Optional<Preferencia> doUsuario = preferenciaRepository.findByChaveAndUsuarioId(chave, usuarioId);
            if (doUsuario.isPresent()) {
                return preferenciaMapper.toResponse(doUsuario.get());
            }
        }
        return preferenciaRepository.findByChaveAndUsuarioIsNull(chave)
            .map(preferenciaMapper::toResponse)
            .orElseThrow(() -> new AplicacaoException(
                "Preferencia nao encontrada para a chave " + chave, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public PreferenciaResponse gravar(String chave, PreferenciaRequest request) {
        Long usuarioId = request.usuarioId();
        Preferencia preferencia = (usuarioId != null
            ? preferenciaRepository.findByChaveAndUsuarioId(chave, usuarioId)
            : preferenciaRepository.findByChaveAndUsuarioIsNull(chave))
            .orElseGet(Preferencia::new);

        preferencia.setChave(chave);
        preferencia.setValor(request.valor());
        if (usuarioId != null) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new AplicacaoException(
                    "Usuario informado nao existe", HttpStatus.BAD_REQUEST));
            preferencia.setUsuario(usuario);
        } else {
            preferencia.setUsuario(null);
        }
        return preferenciaMapper.toResponse(preferenciaRepository.save(preferencia));
    }
}
