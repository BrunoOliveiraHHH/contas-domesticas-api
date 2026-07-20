package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoAporte;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AporteResponse(
    Long id,
    Long investimentoId,
    BigDecimal valor,
    LocalDate data,
    TipoAporte tipo
) {
}
