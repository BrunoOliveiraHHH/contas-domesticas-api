package br.com.contasdomesticas.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Parametro configuravel (chave/valor) com vigencia: indices (SELIC/CDI/IPCA)
 * e aliquotas de imposto (IR_ATE_180, IOF, ...). Sempre vale o de maior
 * vigencia_inicio menor/igual a data consultada.
 */
@Entity
@Table(name = "parametro")
@Getter
@Setter
@NoArgsConstructor
public class Parametro extends BaseEntity {

    @Column(name = "chave", nullable = false, length = 40)
    private String chave;

    @Column(name = "valor", nullable = false, precision = 9, scale = 4)
    private BigDecimal valor;

    @Column(name = "vigencia_inicio", nullable = false)
    private LocalDate vigenciaInicio;

    @Column(name = "descricao", length = 200)
    private String descricao;
}
