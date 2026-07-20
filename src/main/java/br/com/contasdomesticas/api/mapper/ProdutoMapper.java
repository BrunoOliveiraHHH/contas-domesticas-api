package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.Produto;
import br.com.contasdomesticas.api.dto.ProdutoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "unidadeMedidaPadraoId", source = "unidadeMedidaPadrao.id")
    ProdutoResponse toResponse(Produto produto);
}
