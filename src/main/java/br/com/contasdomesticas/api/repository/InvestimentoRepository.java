package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Investimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestimentoRepository extends JpaRepository<Investimento, Long> {
}
