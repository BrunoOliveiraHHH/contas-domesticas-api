package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.Usuario;
import br.com.contasdomesticas.api.dto.UsuarioResponse;
import org.mapstruct.Mapper;

/**
 * Conversao entre a entidade Usuario e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario usuario);
}
