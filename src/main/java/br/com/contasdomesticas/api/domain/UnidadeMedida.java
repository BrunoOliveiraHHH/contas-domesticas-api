package br.com.contasdomesticas.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Unidade de medida dos itens de compra (un, kg, g, L, mL, m). O fator para a
 * unidade base permite comparar preco por unidade.
 */
@Entity
@Table(
    name = "unidade_medida",
    uniqueConstraints = @UniqueConstraint(name = "uk_unidade_medida_sigla", columnNames = "sigla")
)
@Getter
@Setter
@NoArgsConstructor
public class UnidadeMedida extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 60)
    private String nome;

    @Column(name = "sigla", nullable = false, length = 10)
    private String sigla;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoUnidade tipo;

    @Column(name = "fator_para_base", nullable = false, precision = 12, scale = 6)
    private BigDecimal fatorParaBase = BigDecimal.ONE;
}
