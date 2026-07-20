package br.com.contasdomesticas.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ProdutoRequest(
    @NotBlank String nome,
    String descricao,
    Long categoriaId,
    Long unidadeMedidaPadraoId,
    String codigoBarras,
    Boolean ativo
) {
}
