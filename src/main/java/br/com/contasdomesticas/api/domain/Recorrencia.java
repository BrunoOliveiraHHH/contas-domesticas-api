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
 * Modelo de lancamento recorrente (contas fixas). A geracao cria um Lancamento
 * por competencia, de forma idempotente.
 */
@Entity
@Table(name = "recorrencia")
@Getter
@Setter
@NoArgsConstructor
public class Recorrencia extends BaseEntity {

    @Column(name = "descricao", nullable = false, length = 200)
    private String descricao;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoLancamento tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carteira_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_recorrencia_carteira"))
    private Carteira carteira;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_recorrencia_categoria"))
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forma_pagamento_id",
        foreignKey = @ForeignKey(name = "fk_recorrencia_forma_pagamento"))
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequencia", nullable = false, length = 10)
    private Frequencia frequencia;

    @Column(name = "dia_vencimento")
    private Integer diaVencimento;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "ativa", nullable = false)
    private boolean ativa = true;
}
