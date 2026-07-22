package br.com.contasdomesticas.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ProdutoRequest(
    @NotBlank String nome,
    String descricao,
    Long categoriaId,
    Long unidadeMedidaPadraoId,
    String codigoBarras,
    Boolean ativo,
    BigDecimal estoqueMinimo,
    BigDecimal estoqueAtual
) {
}
