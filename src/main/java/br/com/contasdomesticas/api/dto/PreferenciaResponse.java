package br.com.contasdomesticas.api.dto;

public record PreferenciaResponse(
    Long id,
    String chave,
    String valor,
    Long usuarioId
) {
}
