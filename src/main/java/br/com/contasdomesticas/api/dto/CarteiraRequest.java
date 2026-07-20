package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoCarteira;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CarteiraRequest(
    @NotBlank String nome,
    @NotNull TipoCarteira tipo,
    Long donoId,
    BigDecimal saldoInicial,
    String moeda,
    String cor,
    String icone,
    Boolean ativa
) {
}
