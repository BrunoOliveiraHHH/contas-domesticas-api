package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.Mercado;
import br.com.contasdomesticas.api.dto.MercadoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MercadoMapper {

    MercadoResponse toResponse(Mercado mercado);
}
