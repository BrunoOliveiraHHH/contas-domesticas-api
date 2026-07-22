package br.com.contasdomesticas.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProdutoResponse(
    Long id,
    String nome,
    String descricao,
    Long categoriaId,
    Long unidadeMedidaPadraoId,
    String codigoBarras,
    boolean ativo,
    BigDecimal estoqueMinimo,
    BigDecimal estoqueAtual,
    Instant criadoEm,
    String criadoPor
) {
}
