package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Lancamento;
import br.com.contasdomesticas.api.domain.TipoCategoria;
import br.com.contasdomesticas.api.domain.TipoLancamento;
import br.com.contasdomesticas.api.dto.LancamentoResponse;
import br.com.contasdomesticas.api.dto.ReceitaRequest;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.LancamentoMapper;
import br.com.contasdomesticas.api.repository.LancamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceitaService {

    private final LancamentoRepository lancamentoRepository;
    private final LancamentoMapper lancamentoMapper;
    private final ResolvedorLancamento resolvedor;

    @Transactional(readOnly = true)
    public List<LancamentoResponse> listar() {
        return lancamentoRepository.findByTipo(TipoLancamento.RECEITA).stream()
            .map(lancamentoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LancamentoResponse buscarPorId(Long id) {
        return lancamentoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public LancamentoResponse criar(ReceitaRequest request) {
        Lancamento lancamento = new Lancamento();
        aplicar(lancamento, request);
        return lancamentoMapper.toResponse(lancamentoRepository.save(lancamento));
    }

    @Transactional
    public LancamentoResponse atualizar(Long id, ReceitaRequest request) {
        Lancamento lancamento = buscarEntidade(id);
        aplicar(lancamento, request);
        return lancamentoMapper.toResponse(lancamentoRepository.save(lancamento));
    }

    @Transactional
    public void remover(Long id) {
        lancamentoRepository.delete(buscarEntidade(id));
    }

    private void aplicar(Lancamento lancamento, ReceitaRequest request) {
        lancamento.setTipo(TipoLancamento.RECEITA);
        lancamento.setDescricao(request.descricao());
        lancamento.setValor(request.valor());
        lancamento.setDataCompetencia(request.dataCompetencia());
        lancamento.setDataInicio(request.dataInicio());
        lancamento.setDataFim(request.dataFim());
        lancamento.setObservacao(request.observacao());
        lancamento.setCarteira(resolvedor.carteira(request.carteiraId()));
        lancamento.setCategoria(resolvedor.categoria(request.categoriaId(), TipoCategoria.RECEITA));
        lancamento.setFormaPagamento(resolvedor.formaPagamento(request.formaPagamentoId()));
    }

    private Lancamento buscarEntidade(Long id) {
        return lancamentoRepository.findById(id)
            .filter(l -> l.getTipo() == TipoLancamento.RECEITA)
            .orElseThrow(() -> new AplicacaoException(
                "Receita nao encontrada com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
