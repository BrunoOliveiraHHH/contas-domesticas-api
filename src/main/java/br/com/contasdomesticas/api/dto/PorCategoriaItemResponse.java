package br.com.contasdomesticas.api.dto;

import java.math.BigDecimal;

public record PorCategoriaItemResponse(
    Long categoriaId,
    String categoriaNome,
    BigDecimal total,
    BigDecimal percentual
) {
}
