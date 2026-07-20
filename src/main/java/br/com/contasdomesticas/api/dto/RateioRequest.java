package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoRateio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record RateioRequest(
    @NotNull TipoRateio tipo,
    @NotEmpty @Valid List<Participante> participantes
) {
    public record Participante(
        @NotNull Long usuarioId,
        BigDecimal percentual
    ) {
    }
}
