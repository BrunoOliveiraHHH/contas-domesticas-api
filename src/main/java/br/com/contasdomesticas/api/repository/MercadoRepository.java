package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Mercado;
import br.com.contasdomesticas.api.domain.TipoMercado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MercadoRepository extends JpaRepository<Mercado, Long> {

    List<Mercado> findByTipo(TipoMercado tipo);

    Optional<Mercado> findByUuid(UUID uuid);

    List<Mercado> findByAtualizadoEmGreaterThanEqual(Instant desde);
}
