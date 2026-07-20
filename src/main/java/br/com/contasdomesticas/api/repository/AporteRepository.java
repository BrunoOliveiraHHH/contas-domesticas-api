package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Aporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AporteRepository extends JpaRepository<Aporte, Long> {

    List<Aporte> findByInvestimentoId(Long investimentoId);
}
