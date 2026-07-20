package br.com.contasdomesticas.api.dto;

import java.math.BigDecimal;

public record AcertoItemResponse(
    Long usuarioId,
    String usuarioLogin,
    BigDecimal total
) {
}
