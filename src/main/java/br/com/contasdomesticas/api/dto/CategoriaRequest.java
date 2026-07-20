package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoCategoria;
import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
    @NotBlank String nome,
    TipoCategoria tipo,
    Long categoriaPaiId,
    String cor,
    String icone,
    Boolean ativa
) {
}
