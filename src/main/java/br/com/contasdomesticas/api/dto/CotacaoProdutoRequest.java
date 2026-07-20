package br.com.contasdomesticas.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CotacaoProdutoRequest(
    @NotNull Long mercadoId,
    @NotNull @Positive BigDecimal precoUnitario,
    LocalDate data
) {
}
