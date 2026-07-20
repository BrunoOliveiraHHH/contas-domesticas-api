package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.Recorrencia;
import br.com.contasdomesticas.api.dto.RecorrenciaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecorrenciaMapper {

    @Mapping(target = "carteiraId", source = "carteira.id")
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "formaPagamentoId", source = "formaPagamento.id")
    RecorrenciaResponse toResponse(Recorrencia recorrencia);
}
