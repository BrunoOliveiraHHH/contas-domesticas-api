package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Rateio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RateioRepository extends JpaRepository<Rateio, Long> {

    Optional<Rateio> findByLancamentoId(Long lancamentoId);

    List<Rateio> findByLancamento_DataCompetenciaBetween(LocalDate inicio, LocalDate fim);
}
