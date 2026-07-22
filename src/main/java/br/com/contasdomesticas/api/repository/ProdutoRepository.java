package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    /** Produtos ativos com estoque atual abaixo do minimo (precisam de reposicao). */
    @Query("select p from Produto p where p.ativo = true and p.estoqueAtual < p.estoqueMinimo")
    List<Produto> findAbaixoDoMinimo();
}
