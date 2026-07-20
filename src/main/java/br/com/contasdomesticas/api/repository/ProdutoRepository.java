package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
