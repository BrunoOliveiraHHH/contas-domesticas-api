package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Mercado;
import br.com.contasdomesticas.api.dto.MercadoRequest;
import br.com.contasdomesticas.api.dto.MercadoResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.MercadoMapper;
import br.com.contasdomesticas.api.repository.MercadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MercadoService {

    private final MercadoRepository mercadoRepository;
    private final MercadoMapper mercadoMapper;

    @Transactional(readOnly = true)
    public List<MercadoResponse> listar() {
        return mercadoRepository.findAll().stream().map(mercadoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MercadoResponse buscarPorId(Long id) {
        return mercadoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public MercadoResponse criar(MercadoRequest request) {
        Mercado mercado = new Mercado();
        aplicar(mercado, request);
        return mercadoMapper.toResponse(mercadoRepository.save(mercado));
    }

    @Transactional
    public MercadoResponse atualizar(Long id, MercadoRequest request) {
        Mercado mercado = buscarEntidade(id);
        aplicar(mercado, request);
        return mercadoMapper.toResponse(mercadoRepository.save(mercado));
    }

    @Transactional
    public void remover(Long id) {
        mercadoRepository.delete(buscarEntidade(id));
    }

    private void aplicar(Mercado mercado, MercadoRequest request) {
        mercado.setNome(request.nome());
        mercado.setTipo(request.tipo());
        mercado.setEndereco(request.endereco());
        mercado.setBairro(request.bairro());
        mercado.setAtivo(request.ativo() == null || request.ativo());
    }

    private Mercado buscarEntidade(Long id) {
        return mercadoRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Mercado nao encontrado com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
