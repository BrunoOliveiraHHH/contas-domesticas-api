package br.com.contasdomesticas.api.dto;

import jakarta.validation.constraints.NotBlank;

public record PreferenciaRequest(
    @NotBlank String valor,
    Long usuarioId
) {
}
