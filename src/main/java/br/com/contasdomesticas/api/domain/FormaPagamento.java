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
 * Forma de pagamento usada nos lancamentos. Para CREDITO, guarda os dias de
 * fechamento/vencimento (base do parcelamento/fatura).
 */
@Entity
@Table(name = "forma_pagamento")
@Getter
@Setter
@NoArgsConstructor
public class FormaPagamento extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoFormaPagamento tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_id", foreignKey = @ForeignKey(name = "fk_forma_pagamento_carteira"))
    private Carteira carteira;

    @Column(name = "dia_fechamento")
    private Integer diaFechamento;

    @Column(name = "dia_vencimento")
    private Integer diaVencimento;

    @Column(name = "ativa", nullable = false)
    private boolean ativa = true;
}
