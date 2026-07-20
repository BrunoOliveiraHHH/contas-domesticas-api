package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoRateio;

import java.math.BigDecimal;
import java.util.List;

public record RateioResponse(
    Long id,
    Long lancamentoId,
    TipoRateio tipo,
    List<Participante> participantes
) {
    public record Participante(
        Long usuarioId,
        String usuarioLogin,
        BigDecimal percentual,
        BigDecimal valor
    ) {
    }
}
