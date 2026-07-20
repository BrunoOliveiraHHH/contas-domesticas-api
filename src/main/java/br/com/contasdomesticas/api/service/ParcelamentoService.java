package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Carteira;
import br.com.contasdomesticas.api.domain.Categoria;
import br.com.contasdomesticas.api.domain.FormaPagamento;
import br.com.contasdomesticas.api.domain.Lancamento;
import br.com.contasdomesticas.api.domain.StatusLancamento;
import br.com.contasdomesticas.api.domain.TipoCategoria;
import br.com.contasdomesticas.api.domain.TipoLancamento;
import br.com.contasdomesticas.api.dto.LancamentoResponse;
import br.com.contasdomesticas.api.dto.ParcelamentoRequest;
import br.com.contasdomesticas.api.mapper.LancamentoMapper;
import br.com.contasdomesticas.api.repository.LancamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Gera despesas parceladas: N lancamentos ligados por um grupo (uuid), com
 * vencimentos mensais e soma exata (ajuste do centavo na ultima parcela).
 */
@Service
@RequiredArgsConstructor
public class ParcelamentoService {

    private final LancamentoRepository lancamentoRepository;
    private final LancamentoMapper lancamentoMapper;
    private final ResolvedorLancamento resolvedor;

    @Transactional
    public List<LancamentoResponse> gerarParceladas(ParcelamentoRequest request) {
        Carteira carteira = resolvedor.carteira(request.carteiraId());
        Categoria categoria = resolvedor.categoria(request.categoriaId(), TipoCategoria.DESPESA);
        FormaPagamento forma = resolvedor.formaPagamento(request.formaPagamentoId());

        int n = request.parcelas();
        BigDecimal total = request.valorTotal();
        BigDecimal valorParcela = total.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
        UUID grupo = UUID.randomUUID();

        List<Lancamento> criados = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            // ultima parcela absorve o ajuste do arredondamento
            BigDecimal valor = (i < n)
                ? valorParcela
                : total.subtract(valorParcela.multiply(BigDecimal.valueOf(n - 1L)));
            LocalDate vencimento = request.primeiroVencimento().plusMonths(i - 1L);

            Lancamento lancamento = new Lancamento();
            lancamento.setTipo(TipoLancamento.DESPESA);
            lancamento.setDescricao(request.descricao() + " (" + i + "/" + n + ")");
            lancamento.setValor(valor);
            lancamento.setDataCompetencia(vencimento);
            lancamento.setDataVencimento(vencimento);
            lancamento.setCarteira(carteira);
            lancamento.setCategoria(categoria);
            lancamento.setFormaPagamento(forma);
            lancamento.setGrupoParcela(grupo);
            lancamento.setNumeroParcela(i);
            lancamento.setTotalParcelas(n);
            lancamento.setStatus(vencimento.isBefore(LocalDate.now())
                ? StatusLancamento.ATRASADO : StatusLancamento.PENDENTE);

            criados.add(lancamentoRepository.save(lancamento));
        }
        return criados.stream().map(lancamentoMapper::toResponse).toList();
    }
}
