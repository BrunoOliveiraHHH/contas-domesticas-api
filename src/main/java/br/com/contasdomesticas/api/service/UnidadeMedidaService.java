package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.UnidadeMedida;
import br.com.contasdomesticas.api.dto.UnidadeMedidaRequest;
import br.com.contasdomesticas.api.dto.UnidadeMedidaResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.UnidadeMedidaMapper;
import br.com.contasdomesticas.api.repository.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadeMedidaService {

    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final UnidadeMedidaMapper unidadeMedidaMapper;

    @Transactional(readOnly = true)
    public List<UnidadeMedidaResponse> listar() {
        return unidadeMedidaRepository.findAll().stream().map(unidadeMedidaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UnidadeMedidaResponse buscarPorId(Long id) {
        return unidadeMedidaMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public UnidadeMedidaResponse criar(UnidadeMedidaRequest request) {
        if (unidadeMedidaRepository.existsBySigla(request.sigla())) {
            throw new AplicacaoException(
                "Ja existe uma unidade com a sigla " + request.sigla(), HttpStatus.CONFLICT);
        }
        UnidadeMedida unidade = new UnidadeMedida();
        aplicar(unidade, request);
        return unidadeMedidaMapper.toResponse(unidadeMedidaRepository.save(unidade));
    }

    @Transactional
    public UnidadeMedidaResponse atualizar(Long id, UnidadeMedidaRequest request) {
        UnidadeMedida unidade = buscarEntidade(id);
        if (!unidade.getSigla().equals(request.sigla())
            && unidadeMedidaRepository.existsBySigla(request.sigla())) {
            throw new AplicacaoException(
                "Ja existe uma unidade com a sigla " + request.sigla(), HttpStatus.CONFLICT);
        }
        aplicar(unidade, request);
        return unidadeMedidaMapper.toResponse(unidadeMedidaRepository.save(unidade));
    }

    @Transactional
    public void remover(Long id) {
        unidadeMedidaRepository.delete(buscarEntidade(id));
    }

    private void aplicar(UnidadeMedida unidade, UnidadeMedidaRequest request) {
        unidade.setNome(request.nome());
        unidade.setSigla(request.sigla());
        unidade.setTipo(request.tipo());
        unidade.setFatorParaBase(request.fatorParaBase() != null ? request.fatorParaBase() : BigDecimal.ONE);
    }

    private UnidadeMedida buscarEntidade(Long id) {
        return unidadeMedidaRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Unidade de medida nao encontrada com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
