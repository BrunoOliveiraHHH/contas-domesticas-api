package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.UnidadeMedida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadeMedidaRepository extends JpaRepository<UnidadeMedida, Long> {

    boolean existsBySigla(String sigla);
}
