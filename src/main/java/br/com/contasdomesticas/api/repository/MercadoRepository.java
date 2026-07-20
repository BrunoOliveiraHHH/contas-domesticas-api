package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Mercado;
import br.com.contasdomesticas.api.domain.TipoMercado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MercadoRepository extends JpaRepository<Mercado, Long> {

    List<Mercado> findByTipo(TipoMercado tipo);
}
