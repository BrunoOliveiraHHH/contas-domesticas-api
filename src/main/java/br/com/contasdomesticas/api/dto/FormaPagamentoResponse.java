package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoFormaPagamento;

import java.time.Instant;

public record FormaPagamentoResponse(
    Long id,
    String nome,
    TipoFormaPagamento tipo,
    Long carteiraId,
    Integer diaFechamento,
    Integer diaVencimento,
    boolean ativa,
    Instant criadoEm,
    String criadoPor
) {
}
