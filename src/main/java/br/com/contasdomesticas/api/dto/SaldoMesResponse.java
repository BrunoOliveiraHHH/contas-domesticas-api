package br.com.contasdomesticas.api.dto;

import java.math.BigDecimal;

public record SaldoMesResponse(
    String periodo,
    BigDecimal receitas,
    BigDecimal despesas,
    BigDecimal saldo,
    BigDecimal aPagar,
    BigDecimal atrasadas,
    BigDecimal assinaturas
) {
}
