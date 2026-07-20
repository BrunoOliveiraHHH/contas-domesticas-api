package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Parametro;
import br.com.contasdomesticas.api.dto.ParametroRequest;
import br.com.contasdomesticas.api.dto.ParametroResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.ParametroMapper;
import br.com.contasdomesticas.api.repository.ParametroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParametroService {

    private final ParametroRepository parametroRepository;
    private final ParametroMapper parametroMapper;

    @Transactional(readOnly = true)
    public List<ParametroResponse> listar() {
        return parametroRepository.findAll().stream().map(parametroMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ParametroResponse buscarPorId(Long id) {
        return parametroMapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public ParametroResponse buscarVigente(String chave, LocalDate data) {
        return parametroMapper.toResponse(vigente(chave, data));
    }

    /** Valor vigente da chave na data (ou hoje se data == null). Lanca 404 se nao houver. */
    @Transactional(readOnly = true)
    public BigDecimal valorVigente(String chave, LocalDate data) {
        return vigente(chave, data).getValor();
    }

    @Transactional
    public ParametroResponse criar(ParametroRequest request) {
        Parametro parametro = new Parametro();
        aplicar(parametro, request);
        return parametroMapper.toResponse(parametroRepository.save(parametro));
    }

    @Transactional
    public ParametroResponse atualizar(Long id, ParametroRequest request) {
        Parametro parametro = buscarEntidade(id);
        aplicar(parametro, request);
        return parametroMapper.toResponse(parametroRepository.save(parametro));
    }

    @Transactional
    public void remover(Long id) {
        parametroRepository.delete(buscarEntidade(id));
    }

    private void aplicar(Parametro parametro, ParametroRequest request) {
        parametro.setChave(request.chave());
        parametro.setValor(request.valor());
        parametro.setVigenciaInicio(request.vigenciaInicio());
        parametro.setDescricao(request.descricao());
    }

    private Parametro vigente(String chave, LocalDate data) {
        LocalDate referencia = data != null ? data : LocalDate.now();
        return parametroRepository
            .findFirstByChaveAndVigenciaInicioLessThanEqualOrderByVigenciaInicioDesc(chave, referencia)
            .orElseThrow(() -> new AplicacaoException(
                "Nao ha valor vigente para o parametro " + chave, HttpStatus.NOT_FOUND));
    }

    private Parametro buscarEntidade(Long id) {
        return parametroRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Parametro nao encontrado com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
