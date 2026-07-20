package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Categoria;
import br.com.contasdomesticas.api.domain.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByCategoriaPaiIsNull();

    List<Categoria> findByTipo(TipoCategoria tipo);
}
