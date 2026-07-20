package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.Parametro;
import br.com.contasdomesticas.api.dto.ParametroResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParametroMapper {

    ParametroResponse toResponse(Parametro parametro);
}
