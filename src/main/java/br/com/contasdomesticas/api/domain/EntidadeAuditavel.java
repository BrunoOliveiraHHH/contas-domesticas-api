package br.com.contasdomesticas.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Base das entidades com campos de auditoria preenchidos automaticamente
 * (JPA Auditing). O autor vem do {@code AuditorAware} configurado.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class EntidadeAuditavel extends BaseEntity {

    @CreatedDate
    @Column(name = "criado_em", updatable = false)
    private Instant criadoEm;

    @CreatedBy
    @Column(name = "criado_por", updatable = false, length = 100)
    private String criadoPor;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private Instant atualizadoEm;

    @LastModifiedBy
    @Column(name = "atualizado_por", length = 100)
    private String atualizadoPor;
}
