package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Recorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecorrenciaRepository extends JpaRepository<Recorrencia, Long> {
}
