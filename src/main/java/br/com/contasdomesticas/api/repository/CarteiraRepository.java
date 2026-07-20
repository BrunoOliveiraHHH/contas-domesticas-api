package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Carteira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarteiraRepository extends JpaRepository<Carteira, Long> {

    List<Carteira> findByDonoId(Long donoId);
}
