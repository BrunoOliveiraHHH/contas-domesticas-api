package br.com.contasdomesticas.api.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Rateio de uma despesa entre participantes. Um rateio por despesa.
 */
@Entity
@Table(
    name = "rateio",
    uniqueConstraints = @UniqueConstraint(name = "uk_rateio_lancamento", columnNames = "lancamento_id")
)
@Getter
@Setter
@NoArgsConstructor
public class Rateio extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lancamento_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_rateio_lancamento"))
    private Lancamento lancamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 15)
    private TipoRateio tipo;

    @OneToMany(mappedBy = "rateio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipanteRateio> participantes = new ArrayList<>();

    public void adicionar(ParticipanteRateio participante) {
        participante.setRateio(this);
        this.participantes.add(participante);
    }
}
