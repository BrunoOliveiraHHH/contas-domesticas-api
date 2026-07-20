package br.com.contasdomesticas.api.dto;

import java.math.BigDecimal;

public record ImpostoIrResponse(
    int dias,
    String chave,
    BigDecimal aliquota
) {
}
