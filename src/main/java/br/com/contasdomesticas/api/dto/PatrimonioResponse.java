package br.com.contasdomesticas.api.dto;

import java.math.BigDecimal;

public record PatrimonioResponse(
    BigDecimal total
) {
}
