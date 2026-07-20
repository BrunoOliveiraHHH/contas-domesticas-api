package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.Lancamento;
import br.com.contasdomesticas.api.dto.LancamentoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LancamentoMapper {

    @Mapping(target = "carteiraId", source = "carteira.id")
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "formaPagamentoId", source = "formaPagamento.id")
    LancamentoResponse toResponse(Lancamento lancamento);
}
