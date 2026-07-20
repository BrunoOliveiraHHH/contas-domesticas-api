package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoUnidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UnidadeMedidaRequest(
    @NotBlank String nome,
    @NotBlank String sigla,
    @NotNull TipoUnidade tipo,
    BigDecimal fatorParaBase
) {
}
