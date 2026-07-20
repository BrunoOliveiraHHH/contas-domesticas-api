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
 * Investimento. O saldo aplicado e derivado dos aportes (soma aportes - resgates).
 */
@Entity
@Table(name = "investimento")
@Getter
@Setter
@NoArgsConstructor
public class Investimento extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_investimento", nullable = false, length = 25)
    private TipoInvestimento tipoInvestimento;

    @Column(name = "instituicao", length = 120)
    private String instituicao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carteira_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_investimento_carteira"))
    private Carteira carteira;

    @Enumerated(EnumType.STRING)
    @Column(name = "indexador", length = 10)
    private Indexador indexador;

    @Column(name = "taxa_contratada", precision = 9, scale = 4)
    private BigDecimal taxaContratada;

    @Column(name = "data_aplicacao", nullable = false)
    private LocalDate dataAplicacao;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
}
