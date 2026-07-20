package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.UnidadeMedida;
import br.com.contasdomesticas.api.dto.UnidadeMedidaResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnidadeMedidaMapper {

    UnidadeMedidaResponse toResponse(UnidadeMedida unidadeMedida);
}
