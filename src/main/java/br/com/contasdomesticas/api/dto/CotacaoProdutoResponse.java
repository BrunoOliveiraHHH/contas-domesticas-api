package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.OrigemCotacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CotacaoProdutoResponse(
    Long id,
    Long produtoId,
    Long mercadoId,
    BigDecimal precoUnitario,
    LocalDate data,
    OrigemCotacao origem
) {
}
