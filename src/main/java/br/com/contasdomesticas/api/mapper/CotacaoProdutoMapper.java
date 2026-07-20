package br.com.contasdomesticas.api.mapper;

import br.com.contasdomesticas.api.domain.CotacaoProduto;
import br.com.contasdomesticas.api.dto.CotacaoProdutoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CotacaoProdutoMapper {

    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "mercadoId", source = "mercado.id")
    CotacaoProdutoResponse toResponse(CotacaoProduto cotacao);
}
