package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Preferencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenciaRepository extends JpaRepository<Preferencia, Long> {

    Optional<Preferencia> findByChaveAndUsuarioId(String chave, Long usuarioId);

    Optional<Preferencia> findByChaveAndUsuarioIsNull(String chave);
}
