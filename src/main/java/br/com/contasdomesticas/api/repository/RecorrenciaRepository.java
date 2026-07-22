package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Recorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecorrenciaRepository extends JpaRepository<Recorrencia, Long> {

    /** Recorrencias ativas vigentes no periodo (assinaturas/fixas). data_fim nula = infinita. */
    @Query("select r from Recorrencia r where r.ativa = true " +
        "and r.dataInicio <= :fim and (r.dataFim is null or r.dataFim >= :inicio)")
    List<Recorrencia> findAtivasNoPeriodo(
        @Param("inicio") LocalDate inicio,
        @Param("fim") LocalDate fim);
}
