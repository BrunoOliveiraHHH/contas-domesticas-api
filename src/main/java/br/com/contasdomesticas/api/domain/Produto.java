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

/**
 * Produto do catalogo reutilizavel. O item de compra referencia um produto
 * (nao texto livre), entao precos/cotacoes se reaproveitam entre listas.
 */
@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
public class Produto extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "descricao", length = 300)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", foreignKey = @ForeignKey(name = "fk_produto_categoria"))
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_medida_padrao_id",
        foreignKey = @ForeignKey(name = "fk_produto_unidade_medida"))
    private UnidadeMedida unidadeMedidaPadrao;

    @Column(name = "codigo_barras", length = 60)
    private String codigoBarras;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
}
