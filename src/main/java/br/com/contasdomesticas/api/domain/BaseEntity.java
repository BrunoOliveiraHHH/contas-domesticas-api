package br.com.contasdomesticas.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Base de todas as entidades: identificador tecnico (surrogate key) + campos de auditoria.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "criado_em", updatable = false, nullable = false)
    private Instant criadoEm;

    @CreatedBy
    @Column(name = "criado_por", updatable = false, length = 100, nullable = false)
    private String criadoPor;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private Instant atualizadoEm;

    @LastModifiedBy
    @Column(name = "atualizado_por", length = 100)
    private String atualizadoPor;

    // --- Sincronizacao entre instancias (app/front <-> API) ---

    /** Identidade estavel gerada no cliente/servidor; base do upsert por sync. */
    @Column(name = "uuid", unique = true, updatable = false)
    private UUID uuid;

    /** Versao para resolucao de conflito (last-write-wins pela maior versao). */
    @Column(name = "versao", nullable = false, columnDefinition = "bigint default 0")
    private Long versao = 0L;

    /** Soft-delete: funciona como tombstone na sincronizacao. */
    @Column(name = "deletado", nullable = false, columnDefinition = "boolean default false")
    private boolean deletado = false;

    @PrePersist
    void aoPersistir() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseEntity that = (BaseEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }
}
