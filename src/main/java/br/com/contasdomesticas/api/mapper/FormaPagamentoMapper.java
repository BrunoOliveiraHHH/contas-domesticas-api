package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.FormaPagamento;
import br.com.contasdomesticas.api.dto.FormaPagamentoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FormaPagamentoMapper {

    @Mapping(target = "carteiraId", source = "carteira.id")
    FormaPagamentoResponse toResponse(FormaPagamento formaPagamento);
}
