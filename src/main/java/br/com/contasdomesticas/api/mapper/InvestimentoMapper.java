package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.Investimento;
import br.com.contasdomesticas.api.dto.InvestimentoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvestimentoMapper {

    @Mapping(target = "carteiraId", source = "carteira.id")
    InvestimentoResponse toResponse(Investimento investimento);
}
