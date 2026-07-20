package br.com.contasdomesticas.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ItemCompraRequest(
    @NotNull Long produtoId,
    @NotNull @Positive BigDecimal quantidade,
    Long unidadeMedidaId
) {
}
