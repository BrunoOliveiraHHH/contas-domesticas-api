package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Lancamento;
import br.com.contasdomesticas.api.domain.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    List<Lancamento> findByTipo(TipoLancamento tipo);

    /**
     * Lancamentos "vigentes" no periodo: se tem validade (data_inicio/data_fim), conta
     * quando o periodo cai dentro dela (data_fim nula = infinita); senao, usa a competencia.
     */
    @Query("select l from Lancamento l where l.tipo = :tipo and " +
        "((l.dataInicio is not null and l.dataInicio <= :fim and (l.dataFim is null or l.dataFim >= :inicio)) " +
        "or (l.dataInicio is null and l.dataCompetencia between :inicio and :fim))")
    List<Lancamento> findVigentesNoPeriodo(
        @Param("tipo") TipoLancamento tipo,
        @Param("inicio") LocalDate inicio,
        @Param("fim") LocalDate fim);

    List<Lancamento> findByTipoAndDataCompetenciaBetween(
        TipoLancamento tipo, LocalDate inicio, LocalDate fim);

    List<Lancamento> findByDataCompetenciaBetween(LocalDate inicio, LocalDate fim);

    List<Lancamento> findByCarteiraId(Long carteiraId);

    Optional<Lancamento> findByRecorrenciaIdAndDataCompetencia(Long recorrenciaId, LocalDate dataCompetencia);
}
