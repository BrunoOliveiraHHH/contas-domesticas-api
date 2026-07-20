package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.ItemCompra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemCompraRepository extends JpaRepository<ItemCompra, Long> {

    List<ItemCompra> findByListaCompraId(Long listaCompraId);
}
