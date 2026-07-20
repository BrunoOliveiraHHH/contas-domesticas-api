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

/**
 * Categoria dos lancamentos, hierarquica (categoria -> subcategorias).
 * Subcategoria herda o tipo da categoria pai.
 */
@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
public class Categoria extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoCategoria tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_pai_id", foreignKey = @ForeignKey(name = "fk_categoria_pai"))
    private Categoria categoriaPai;

    @Column(name = "cor", length = 20)
    private String cor;

    @Column(name = "icone", length = 40)
    private String icone;

    @Column(name = "ativa", nullable = false)
    private boolean ativa = true;
}
