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
import java.util.UUID;

/**
 * Nucleo compartilhado por receita e despesa. O escopo (familiar/individual) e
 * herdado da carteira. Status/vencimento/pagamento so se aplicam a despesa.
 */
@Entity
@Table(name = "lancamento")
@Getter
@Setter
@NoArgsConstructor
public class Lancamento extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoLancamento tipo;

    @Column(name = "descricao", nullable = false, length = 200)
    private String descricao;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_competencia", nullable = false)
    private LocalDate dataCompetencia;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    // Validade (usada em receitas recorrentes). data_fim nula = validade infinita.
    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    private StatusLancamento status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carteira_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_lancamento_carteira"))
    private Carteira carteira;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_lancamento_categoria"))
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forma_pagamento_id",
        foreignKey = @ForeignKey(name = "fk_lancamento_forma_pagamento"))
    private FormaPagamento formaPagamento;

    @Column(name = "observacao", length = 300)
    private String observacao;

    @Column(name = "anexo_url", length = 300)
    private String anexoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorrencia_id",
        foreignKey = @ForeignKey(name = "fk_lancamento_recorrencia"))
    private Recorrencia recorrencia;

    @Column(name = "grupo_parcela")
    private UUID grupoParcela;

    @Column(name = "numero_parcela")
    private Integer numeroParcela;

    @Column(name = "total_parcelas")
    private Integer totalParcelas;
}
