package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.ListaCompra;
import br.com.contasdomesticas.api.dto.ListaCompraResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ListaCompraMapper {

    @Mapping(target = "carteiraId", source = "carteira.id")
    ListaCompraResponse toResponse(ListaCompra listaCompra);
}
