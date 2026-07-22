package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Categoria;
import br.com.contasdomesticas.api.domain.Frequencia;
import br.com.contasdomesticas.api.domain.Lancamento;
import br.com.contasdomesticas.api.domain.Recorrencia;
import br.com.contasdomesticas.api.domain.TipoLancamento;
import br.com.contasdomesticas.api.dto.PorCategoriaItemResponse;
import br.com.contasdomesticas.api.dto.SaldoMesResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.repository.LancamentoRepository;
import br.com.contasdomesticas.api.repository.RecorrenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final LancamentoRepository lancamentoRepository;
    private final RecorrenciaRepository recorrenciaRepository;

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
        // Recorrencias/assinaturas ativas: contam no dashboard sem gerar lancamento.
        // Se a recorrencia ja tem lancamento gerado no mes, o lancamento ja conta (evita duplicar).
        Set<Long> jaGeradas = new HashSet<>(lancamentoRepository.recorrenciasComLancamentoNoPeriodo(ini, fim));
        for (Recorrencia r : recorrenciaRepository.findAtivasNoPeriodo(ini, fim)) {
            if (jaGeradas.contains(r.getId())) {
                continue;
            }
            if (carteiraId != null && !carteiraId.equals(r.getCarteira().getId())) {
                continue;
            }
            BigDecimal contrib = r.getValor().multiply(BigDecimal.valueOf(ocorrenciasNoMes(r, ym)));
            if (r.getTipo() == TipoLancamento.RECEITA) {
                receitas = receitas.add(contrib);
            } else {
                despesas = despesas.add(contrib);
            }
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
        // Recorrencias/assinaturas ativas do mesmo tipo entram no agrupamento (sem duplicar geradas).
        Set<Long> jaGeradas = new HashSet<>(
            lancamentoRepository.recorrenciasComLancamentoNoPeriodo(ym.atDay(1), ym.atEndOfMonth()));
        for (Recorrencia r : recorrenciaRepository.findAtivasNoPeriodo(ym.atDay(1), ym.atEndOfMonth())) {
            if (r.getTipo() != tipoFiltro || jaGeradas.contains(r.getId())) {
                continue;
            }
            BigDecimal contrib = r.getValor().multiply(BigDecimal.valueOf(ocorrenciasNoMes(r, ym)));
            if (contrib.signum() == 0) {
                continue;
            }
            Categoria categoria = r.getCategoria();
            Agrupado agrupado = porCategoria.computeIfAbsent(
                categoria.getId(), k -> new Agrupado(categoria.getNome()));
            agrupado.total = agrupado.total.add(contrib);
            totalGeral = totalGeral.add(contrib);
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

    /** Quantas vezes a recorrencia ocorre no mes (MENSAL=1, SEMANAL~4, ANUAL=1 no mes de inicio). */
    private int ocorrenciasNoMes(Recorrencia r, YearMonth ym) {
        Frequencia f = r.getFrequencia();
        if (f == Frequencia.MENSAL) {
            return 1;
        }
        if (f == Frequencia.SEMANAL) {
            return 4;
        }
        // ANUAL: conta no mes de aniversario (mes de data_inicio).
        return r.getDataInicio().getMonthValue() == ym.getMonthValue() ? 1 : 0;
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
