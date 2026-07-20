package br.com.contasdomesticas.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ParametroResponse(
    Long id,
    String chave,
    BigDecimal valor,
    LocalDate vigenciaInicio,
    String descricao,
    Instant criadoEm,
    String criadoPor
) {
}
