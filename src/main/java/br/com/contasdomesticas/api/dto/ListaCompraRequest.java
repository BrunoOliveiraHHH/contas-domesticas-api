package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoLista;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ListaCompraRequest(
    @NotBlank String nome,
    @NotNull TipoLista tipo,
    @NotNull Long carteiraId,
    LocalDate data
) {
}
