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

/**
 * Carteira: unidade que separa o escopo familiar x individual e agrupa saldos.
 * O saldo atual e derivado (saldo inicial +/- lancamentos) e nao e persistido.
 */
@Entity
@Table(name = "carteira")
@Getter
@Setter
@NoArgsConstructor
public class Carteira extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoCarteira tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dono_id", foreignKey = @ForeignKey(name = "fk_carteira_dono"))
    private Usuario dono;

    @Column(name = "saldo_inicial", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(name = "moeda", nullable = false, length = 3)
    private String moeda = "BRL";

    @Column(name = "cor", length = 20)
    private String cor;

    @Column(name = "icone", length = 40)
    private String icone;

    @Column(name = "ativa", nullable = false)
    private boolean ativa = true;
}
