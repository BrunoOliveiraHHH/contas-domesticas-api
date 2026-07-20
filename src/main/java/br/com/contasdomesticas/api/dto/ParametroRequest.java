package br.com.contasdomesticas.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParametroRequest(
    @NotBlank String chave,
    @NotNull BigDecimal valor,
    @NotNull LocalDate vigenciaInicio,
    String descricao
) {
}
