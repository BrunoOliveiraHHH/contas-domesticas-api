package br.com.contasdomesticas.api.dto;

import java.time.Instant;

public record ProdutoResponse(
    Long id,
    String nome,
    String descricao,
    Long categoriaId,
    Long unidadeMedidaPadraoId,
    String codigoBarras,
    boolean ativo,
    Instant criadoEm,
    String criadoPor
) {
}
