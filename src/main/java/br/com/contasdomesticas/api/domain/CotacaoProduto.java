package br.com.contasdomesticas.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Preco de um produto em um estabelecimento (reutilizavel entre listas).
 * origem COTACAO = planejada; COMPRA = preco realizado no fechamento.
 */
@Entity
@Table(name = "cotacao_produto")
@Getter
@Setter
@NoArgsConstructor
public class CotacaoProduto extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_cotacao_produto"))
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mercado_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_cotacao_mercado"))
    private Mercado mercado;

    @Column(name = "preco_unitario", nullable = false, precision = 15, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem", nullable = false, length = 10)
    private OrigemCotacao origem = OrigemCotacao.COTACAO;
}
