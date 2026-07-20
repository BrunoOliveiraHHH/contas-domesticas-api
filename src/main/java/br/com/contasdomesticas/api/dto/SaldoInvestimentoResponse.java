package br.com.contasdomesticas.api.dto;

import java.math.BigDecimal;

public record SaldoInvestimentoResponse(
    Long investimentoId,
    String nome,
    BigDecimal saldoAplicado
) {
}
