package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.Aporte;
import br.com.contasdomesticas.api.dto.AporteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AporteMapper {

    @Mapping(target = "investimentoId", source = "investimento.id")
    AporteResponse toResponse(Aporte aporte);
}
