package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Lancamento;
import br.com.contasdomesticas.api.domain.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    List<Lancamento> findByTipo(TipoLancamento tipo);

    List<Lancamento> findByTipoAndDataCompetenciaBetween(
        TipoLancamento tipo, LocalDate inicio, LocalDate fim);

    List<Lancamento> findByCarteiraId(Long carteiraId);
}
