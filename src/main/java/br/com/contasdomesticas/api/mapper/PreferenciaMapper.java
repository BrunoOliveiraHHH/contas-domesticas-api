package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.Preferencia;
import br.com.contasdomesticas.api.dto.PreferenciaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PreferenciaMapper {

    @Mapping(target = "usuarioId", source = "usuario.id")
    PreferenciaResponse toResponse(Preferencia preferencia);
}
