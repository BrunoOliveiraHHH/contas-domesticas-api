package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.Indexador;
import br.com.contasdomesticas.api.domain.TipoInvestimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestimentoRequest(
    @NotBlank String nome,
    @NotNull TipoInvestimento tipoInvestimento,
    String instituicao,
    @NotNull Long carteiraId,
    Indexador indexador,
    BigDecimal taxaContratada,
    @NotNull LocalDate dataAplicacao,
    LocalDate dataVencimento
) {
}
