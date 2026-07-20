package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoMercado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MercadoRequest(
    @NotBlank String nome,
    @NotNull TipoMercado tipo,
    String endereco,
    String bairro,
    Boolean ativo
) {
}
