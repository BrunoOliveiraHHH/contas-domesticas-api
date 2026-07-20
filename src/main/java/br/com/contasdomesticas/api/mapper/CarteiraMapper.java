package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.Carteira;
import br.com.contasdomesticas.api.dto.CarteiraResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarteiraMapper {

    @Mapping(target = "donoId", source = "dono.id")
    @Mapping(target = "donoLogin", source = "dono.login")
    CarteiraResponse toResponse(Carteira carteira);
}
