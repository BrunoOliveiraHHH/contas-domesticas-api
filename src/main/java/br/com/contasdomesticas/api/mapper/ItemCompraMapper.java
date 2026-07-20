package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.ItemCompra;
import br.com.contasdomesticas.api.dto.ItemCompraResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemCompraMapper {

    @Mapping(target = "listaCompraId", source = "listaCompra.id")
    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "produtoNome", source = "produto.nome")
    @Mapping(target = "unidadeMedidaId", source = "unidadeMedida.id")
    @Mapping(target = "mercadoEscolhidoId", source = "mercadoEscolhido.id")
    ItemCompraResponse toResponse(ItemCompra item);
}
