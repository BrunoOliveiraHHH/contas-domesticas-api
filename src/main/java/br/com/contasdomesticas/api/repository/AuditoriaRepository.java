package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
}
