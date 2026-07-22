package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Categoria;
import br.com.contasdomesticas.api.domain.Lancamento;
import br.com.contasdomesticas.api.domain.TipoLancamento;
import br.com.contasdomesticas.api.dto.PorCategoriaItemResponse;
import br.com.contasdomesticas.api.dto.SaldoMesResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.repository.LancamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final LancamentoRepository lancamentoRepository;

    @Transactional(readOnly = true)
    public SaldoMesResponse saldoDoMes(String periodo, Long carteiraId) {
        YearMonth ym = periodo(periodo);
        LocalDate ini = ym.atDay(1);
        LocalDate fim = ym.atEndOfMonth();
        BigDecimal receitas = BigDecimal.ZERO;
        BigDecimal despesas = BigDecimal.ZERO;

        // Receitas: contam pela validade (data_inicio/data_fim), com fallback a competencia.
        for (Lancamento l : lancamentoRepository.findVigentesNoPeriodo(TipoLancamento.RECEITA, ini, fim)) {
            if (carteiraId != null && !carteiraId.equals(l.getCarteira().getId())) {
                continue;
            }
            receitas = receitas.add(l.getValor());
        }
        // Despesas: pela competencia do mes.
        for (Lancamento l : lancamentoRepository.findByTipoAndDataCompetenciaBetween(TipoLancamento.DESPESA, ini, fim)) {
            if (carteiraId != null && !carteiraId.equals(l.getCarteira().getId())) {
                continue;
            }
            despesas = despesas.add(l.getValor());
        }
        return new SaldoMesResponse(periodo,
            escala(receitas), escala(despesas), escala(receitas.subtract(despesas)));
    }

    @Transactional(readOnly = true)
    public List<PorCategoriaItemResponse> porCategoria(String periodo, TipoLancamento tipo) {
        YearMonth ym = periodo(periodo);
        TipoLancamento tipoFiltro = tipo != null ? tipo : TipoLancamento.DESPESA;

        List<Lancamento> lancamentos = lancamentoRepository
            .findByTipoAndDataCompetenciaBetween(tipoFiltro, ym.atDay(1), ym.atEndOfMonth());

        Map<Long, Agrupado> porCategoria = new LinkedHashMap<>();
        BigDecimal totalGeral = BigDecimal.ZERO;
        for (Lancamento l : lancamentos) {
            Categoria categoria = l.getCategoria();
            Agrupado agrupado = porCategoria.computeIfAbsent(
                categoria.getId(), k -> new Agrupado(categoria.getNome()));
            agrupado.total = agrupado.total.add(l.getValor());
            totalGeral = totalGeral.add(l.getValor());
        }

        BigDecimal total = totalGeral;
        return porCategoria.entrySet().stream()
            .map(e -> new PorCategoriaItemResponse(
                e.getKey(),
                e.getValue().nome,
                escala(e.getValue().total),
                percentual(e.getValue().total, total)))
            .toList();
    }

    private BigDecimal percentual(BigDecimal parte, BigDecimal total) {
        if (total.signum() == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return parte.multiply(CEM).divide(total, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal escala(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private YearMonth periodo(String periodo) {
        try {
            return YearMonth.parse(periodo);
        } catch (RuntimeException e) {
            throw new AplicacaoException("Periodo invalido (use YYYY-MM)", HttpStatus.BAD_REQUEST);
        }
    }

    private static final class Agrupado {
        private final String nome;
        private BigDecimal total = BigDecimal.ZERO;

        private Agrupado(String nome) {
            this.nome = nome;
        }
    }
}
