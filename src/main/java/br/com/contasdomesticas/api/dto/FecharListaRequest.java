package br.com.contasdomesticas.api.dto;

import jakarta.validation.constraints.NotNull;

public record FecharListaRequest(
    @NotNull Long categoriaId
) {
}
