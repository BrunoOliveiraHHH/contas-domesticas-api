package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Parametro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ParametroRepository extends JpaRepository<Parametro, Long> {

    Optional<Parametro> findFirstByChaveAndVigenciaInicioLessThanEqualOrderByVigenciaInicioDesc(
        String chave, LocalDate data);
}
