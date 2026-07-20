package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.Categoria;
import br.com.contasdomesticas.api.dto.CategoriaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    @Mapping(target = "categoriaPaiId", source = "categoriaPai.id")
    CategoriaResponse toResponse(Categoria categoria);
}
