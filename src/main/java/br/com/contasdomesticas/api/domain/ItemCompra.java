package br.com.contasdomesticas.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Item de uma lista de compra. Referencia um produto do catalogo; o
 * estabelecimento e o preco vem da cotacao escolhida.
 */
@Entity
@Table(name = "item_compra")
@Getter
@Setter
@NoArgsConstructor
public class ItemCompra extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lista_compra_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_item_compra_lista"))
    private ListaCompra listaCompra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_item_compra_produto"))
    private Produto produto;

    @Column(name = "quantidade", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_medida_id",
        foreignKey = @ForeignKey(name = "fk_item_compra_unidade_medida"))
    private UnidadeMedida unidadeMedida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mercado_escolhido_id",
        foreignKey = @ForeignKey(name = "fk_item_compra_mercado"))
    private Mercado mercadoEscolhido;

    @Column(name = "preco_unitario", precision = 15, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "comprado", nullable = false)
    private boolean comprado = false;
}
