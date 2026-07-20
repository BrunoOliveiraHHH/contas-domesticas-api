package br.com.contasdomesticas.api.dto;

import java.math.BigDecimal;

public record ItemCompraResponse(
    Long id,
    Long listaCompraId,
    Long produtoId,
    String produtoNome,
    BigDecimal quantidade,
    Long unidadeMedidaId,
    Long mercadoEscolhidoId,
    BigDecimal precoUnitario,
    boolean comprado
) {
}
