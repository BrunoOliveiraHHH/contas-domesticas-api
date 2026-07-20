package br.com.contasdomesticas.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Preferencia (chave/valor). Quando usuario e nulo, e global; quando preenchido,
 * sobrepoe a global para aquele usuario (fallback usuario -> global -> default).
 */
@Entity
@Table(
    name = "preferencia",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_preferencia_chave_usuario", columnNames = {"chave", "usuario_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class Preferencia extends BaseEntity {

    @Column(name = "chave", nullable = false, length = 40)
    private String chave;

    @Column(name = "valor", nullable = false, length = 120)
    private String valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", foreignKey = @ForeignKey(name = "fk_preferencia_usuario"))
    private Usuario usuario;
}
