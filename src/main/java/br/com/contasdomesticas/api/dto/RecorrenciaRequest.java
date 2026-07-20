package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.Frequencia;
import br.com.contasdomesticas.api.domain.TipoLancamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecorrenciaRequest(
    @NotBlank String descricao,
    @NotNull @Positive BigDecimal valor,
    @NotNull TipoLancamento tipo,
    @NotNull Long carteiraId,
    @NotNull Long categoriaId,
    Long formaPagamentoId,
    @NotNull Frequencia frequencia,
    Integer diaVencimento,
    @NotNull LocalDate dataInicio,
    LocalDate dataFim,
    Boolean ativa
) {
}
