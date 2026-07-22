package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Lancamento;
import br.com.contasdomesticas.api.domain.Recorrencia;
import br.com.contasdomesticas.api.domain.StatusLancamento;
import br.com.contasdomesticas.api.domain.TipoCategoria;
import br.com.contasdomesticas.api.domain.TipoLancamento;
import br.com.contasdomesticas.api.dto.LancamentoResponse;
import br.com.contasdomesticas.api.dto.RecorrenciaRequest;
import br.com.contasdomesticas.api.dto.RecorrenciaResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.LancamentoMapper;
import br.com.contasdomesticas.api.mapper.RecorrenciaMapper;
import br.com.contasdomesticas.api.repository.LancamentoRepository;
import br.com.contasdomesticas.api.repository.RecorrenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecorrenciaService {

    private final RecorrenciaRepository recorrenciaRepository;
    private final LancamentoRepository lancamentoRepository;
    private final RecorrenciaMapper recorrenciaMapper;
    private final LancamentoMapper lancamentoMapper;
    private final ResolvedorLancamento resolvedor;

    @Transactional(readOnly = true)
    public List<RecorrenciaResponse> listar() {
        return recorrenciaRepository.findAll().stream().map(recorrenciaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RecorrenciaResponse buscarPorId(Long id) {
        return recorrenciaMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public RecorrenciaResponse criar(RecorrenciaRequest request) {
        Recorrencia recorrencia = new Recorrencia();
        aplicar(recorrencia, request);
        return recorrenciaMapper.toResponse(recorrenciaRepository.save(recorrencia));
    }

    @Transactional
    public RecorrenciaResponse atualizar(Long id, RecorrenciaRequest request) {
        Recorrencia recorrencia = buscarEntidade(id);
        aplicar(recorrencia, request);
        return recorrenciaMapper.toResponse(recorrenciaRepository.save(recorrencia));
    }

    @Transactional
    public void remover(Long id) {
        Recorrencia recorrencia = buscarEntidade(id);
        // Desvincula os lancamentos ja gerados (preserva os registros) antes de remover a recorrencia
        lancamentoRepository.desvincularRecorrencia(id);
        recorrenciaRepository.delete(recorrencia);
    }

    /** Gera o lancamento da competencia informada (idempotente por recorrencia+competencia). */
    @Transactional
    public LancamentoResponse gerar(Long id, LocalDate competencia) {
        Recorrencia recorrencia = buscarEntidade(id);

        return lancamentoRepository.findByRecorrenciaIdAndDataCompetencia(id, competencia)
            .map(lancamentoMapper::toResponse)
            .orElseGet(() -> lancamentoMapper.toResponse(gerarLancamento(recorrencia, competencia)));
    }

    private Lancamento gerarLancamento(Recorrencia recorrencia, LocalDate competencia) {
        Lancamento lancamento = new Lancamento();
        lancamento.setRecorrencia(recorrencia);
        lancamento.setTipo(recorrencia.getTipo());
        lancamento.setDescricao(recorrencia.getDescricao());
        lancamento.setValor(recorrencia.getValor());
        lancamento.setDataCompetencia(competencia);
        lancamento.setCarteira(recorrencia.getCarteira());
        lancamento.setCategoria(recorrencia.getCategoria());
        lancamento.setFormaPagamento(recorrencia.getFormaPagamento());

        if (recorrencia.getTipo() == TipoLancamento.DESPESA) {
            LocalDate vencimento = vencimentoDa(competencia, recorrencia.getDiaVencimento());
            lancamento.setDataVencimento(vencimento);
            lancamento.setStatus(vencimento.isBefore(LocalDate.now())
                ? StatusLancamento.ATRASADO : StatusLancamento.PENDENTE);
        }
        return lancamentoRepository.save(lancamento);
    }

    private LocalDate vencimentoDa(LocalDate competencia, Integer diaVencimento) {
        if (diaVencimento == null) {
            return competencia;
        }
        int dia = Math.min(diaVencimento, competencia.lengthOfMonth());
        return competencia.withDayOfMonth(dia);
    }

    private void aplicar(Recorrencia recorrencia, RecorrenciaRequest request) {
        recorrencia.setDescricao(request.descricao());
        recorrencia.setValor(request.valor());
        recorrencia.setTipo(request.tipo());
        recorrencia.setFrequencia(request.frequencia());
        recorrencia.setDiaVencimento(request.diaVencimento());
        recorrencia.setDataInicio(request.dataInicio());
        recorrencia.setDataFim(request.dataFim());
        recorrencia.setAtiva(request.ativa() == null || request.ativa());
        recorrencia.setCarteira(resolvedor.carteira(request.carteiraId()));
        recorrencia.setCategoria(resolvedor.categoria(request.categoriaId(), tipoCategoria(request.tipo())));
        recorrencia.setFormaPagamento(resolvedor.formaPagamento(request.formaPagamentoId()));
    }

    private TipoCategoria tipoCategoria(TipoLancamento tipo) {
        return tipo == TipoLancamento.RECEITA ? TipoCategoria.RECEITA : TipoCategoria.DESPESA;
    }

    private Recorrencia buscarEntidade(Long id) {
        return recorrenciaRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Recorrencia nao encontrada com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
