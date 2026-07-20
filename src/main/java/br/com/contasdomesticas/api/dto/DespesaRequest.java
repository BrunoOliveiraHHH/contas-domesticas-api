package br.com.contasdomesticas.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaRequest(
    @NotBlank String descricao,
    @NotNull @Positive BigDecimal valor,
    @NotNull LocalDate dataCompetencia,
    @NotNull Long carteiraId,
    @NotNull Long categoriaId,
    Long formaPagamentoId,
    LocalDate dataVencimento,
    LocalDate dataPagamento,
    String observacao
) {
}
