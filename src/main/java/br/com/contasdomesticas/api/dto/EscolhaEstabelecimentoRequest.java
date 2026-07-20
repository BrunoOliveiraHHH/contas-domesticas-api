package br.com.contasdomesticas.api.dto;

import jakarta.validation.constraints.NotNull;

public record EscolhaEstabelecimentoRequest(
    @NotNull Long mercadoId
) {
}
