package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoAporte;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AporteRequest(
    @NotNull @Positive BigDecimal valor,
    @NotNull LocalDate data,
    @NotNull TipoAporte tipo
) {
}
