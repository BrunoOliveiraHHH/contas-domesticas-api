package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.ListaCompra;
import br.com.contasdomesticas.api.domain.StatusLista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListaCompraRepository extends JpaRepository<ListaCompra, Long> {

    List<ListaCompra> findByStatus(StatusLista status);
}
