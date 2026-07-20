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

import java.math.BigDecimal;

@Entity
@Table(name = "participante_rateio")
@Getter
@Setter
@NoArgsConstructor
public class ParticipanteRateio extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rateio_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_participante_rateio_rateio"))
    private Rateio rateio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_participante_rateio_usuario"))
    private Usuario usuario;

    @Column(name = "percentual", precision = 7, scale = 4)
    private BigDecimal percentual;

    @Column(name = "valor", precision = 15, scale = 2)
    private BigDecimal valor;
}
