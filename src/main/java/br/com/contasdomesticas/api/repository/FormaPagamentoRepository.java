package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.FormaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long> {
}
