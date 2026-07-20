package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.StatusLancamento;
import br.com.contasdomesticas.api.domain.TipoLancamento;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record LancamentoResponse(
    Long id,
    TipoLancamento tipo,
    String descricao,
    BigDecimal valor,
    LocalDate dataCompetencia,
    LocalDate dataVencimento,
    LocalDate dataPagamento,
    StatusLancamento status,
    Long carteiraId,
    Long categoriaId,
    Long formaPagamentoId,
    String observacao,
    Instant criadoEm,
    String criadoPor
) {
}
