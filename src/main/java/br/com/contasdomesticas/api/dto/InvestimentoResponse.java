package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.Indexador;
import br.com.contasdomesticas.api.domain.TipoInvestimento;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record InvestimentoResponse(
    Long id,
    String nome,
    TipoInvestimento tipoInvestimento,
    String instituicao,
    Long carteiraId,
    Indexador indexador,
    BigDecimal taxaContratada,
    LocalDate dataAplicacao,
    LocalDate dataVencimento,
    Instant criadoEm,
    String criadoPor
) {
}
