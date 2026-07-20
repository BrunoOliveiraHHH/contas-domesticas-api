package br.com.contasdomesticas.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParcelamentoRequest(
    @NotBlank String descricao,
    @NotNull @Positive BigDecimal valorTotal,
    @NotNull @Min(2) Integer parcelas,
    @NotNull LocalDate primeiroVencimento,
    @NotNull Long carteiraId,
    @NotNull Long categoriaId,
    Long formaPagamentoId
) {
}
