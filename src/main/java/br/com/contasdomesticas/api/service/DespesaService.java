package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Lancamento;
import br.com.contasdomesticas.api.domain.StatusLancamento;
import br.com.contasdomesticas.api.domain.TipoCategoria;
import br.com.contasdomesticas.api.domain.TipoLancamento;
import br.com.contasdomesticas.api.dto.DespesaRequest;
import br.com.contasdomesticas.api.dto.LancamentoResponse;
import br.com.contasdomesticas.api.dto.PagamentoRequest;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.LancamentoMapper;
import br.com.contasdomesticas.api.repository.LancamentoRepository;
import br.com.contasdomesticas.api.repository.RateioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DespesaService {

    private final LancamentoRepository lancamentoRepository;
    private final RateioRepository rateioRepository;
    private final LancamentoMapper lancamentoMapper;
    private final ResolvedorLancamento resolvedor;

    @Transactional(readOnly = true)
    public List<LancamentoResponse> listar() {
        return lancamentoRepository.findByTipo(TipoLancamento.DESPESA).stream()
            .map(lancamentoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LancamentoResponse buscarPorId(Long id) {
        return lancamentoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public LancamentoResponse criar(DespesaRequest request) {
        Lancamento lancamento = new Lancamento();
        aplicar(lancamento, request);
        return lancamentoMapper.toResponse(lancamentoRepository.save(lancamento));
    }

    @Transactional
    public LancamentoResponse atualizar(Long id, DespesaRequest request) {
        Lancamento lancamento = buscarEntidade(id);
        aplicar(lancamento, request);
        return lancamentoMapper.toResponse(lancamentoRepository.save(lancamento));
    }

    @Transactional
    public LancamentoResponse marcarComoPago(Long id, PagamentoRequest request) {
        Lancamento lancamento = buscarEntidade(id);
        LocalDate pagamento = request != null && request.dataPagamento() != null
            ? request.dataPagamento() : LocalDate.now();
        lancamento.setDataPagamento(pagamento);
        lancamento.setStatus(StatusLancamento.PAGO);
        return lancamentoMapper.toResponse(lancamentoRepository.save(lancamento));
    }

    @Transactional
    public void remover(Long id) {
        Lancamento despesa = buscarEntidade(id);
        // Remove o rateio (e seus participantes, via orphanRemoval) antes da despesa
        rateioRepository.findByLancamentoId(id).ifPresent(rateioRepository::delete);
        lancamentoRepository.delete(despesa);
    }

    private void aplicar(Lancamento lancamento, DespesaRequest request) {
        lancamento.setTipo(TipoLancamento.DESPESA);
        lancamento.setDescricao(request.descricao());
        lancamento.setValor(request.valor());
        lancamento.setDataCompetencia(request.dataCompetencia());
        lancamento.setObservacao(request.observacao());
        lancamento.setCarteira(resolvedor.carteira(request.carteiraId()));
        lancamento.setCategoria(resolvedor.categoria(request.categoriaId(), TipoCategoria.DESPESA));
        lancamento.setFormaPagamento(resolvedor.formaPagamento(request.formaPagamentoId()));
        lancamento.setDataVencimento(request.dataVencimento());
        lancamento.setDataPagamento(request.dataPagamento());
        lancamento.setStatus(derivarStatus(lancamento));
    }

    private StatusLancamento derivarStatus(Lancamento lancamento) {
        if (lancamento.getDataPagamento() != null) {
            return StatusLancamento.PAGO;
        }
        if (lancamento.getDataVencimento() != null
            && lancamento.getDataVencimento().isBefore(LocalDate.now())) {
            return StatusLancamento.ATRASADO;
        }
        return StatusLancamento.PENDENTE;
    }

    private Lancamento buscarEntidade(Long id) {
        return lancamentoRepository.findById(id)
            .filter(l -> l.getTipo() == TipoLancamento.DESPESA)
            .orElseThrow(() -> new AplicacaoException(
                "Despesa nao encontrada com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
