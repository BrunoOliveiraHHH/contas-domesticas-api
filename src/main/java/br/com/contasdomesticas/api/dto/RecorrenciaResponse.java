package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.Frequencia;
import br.com.contasdomesticas.api.domain.TipoLancamento;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record RecorrenciaResponse(
    Long id,
    String descricao,
    BigDecimal valor,
    TipoLancamento tipo,
    Long carteiraId,
    Long categoriaId,
    Long formaPagamentoId,
    Frequencia frequencia,
    Integer diaVencimento,
    LocalDate dataInicio,
    LocalDate dataFim,
    boolean ativa,
    Instant criadoEm,
    String criadoPor
) {
}
