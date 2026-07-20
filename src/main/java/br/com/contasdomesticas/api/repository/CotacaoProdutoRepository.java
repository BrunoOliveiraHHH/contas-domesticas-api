package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.CotacaoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CotacaoProdutoRepository extends JpaRepository<CotacaoProduto, Long> {

    List<CotacaoProduto> findByProdutoIdOrderByPrecoUnitarioAsc(Long produtoId);

    Optional<CotacaoProduto> findFirstByProdutoIdAndMercadoIdOrderByDataDesc(Long produtoId, Long mercadoId);
}
