package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoUnidade;

import java.math.BigDecimal;
import java.time.Instant;

public record UnidadeMedidaResponse(
    Long id,
    String nome,
    String sigla,
    TipoUnidade tipo,
    BigDecimal fatorParaBase,
    Instant criadoEm,
    String criadoPor
) {
}
