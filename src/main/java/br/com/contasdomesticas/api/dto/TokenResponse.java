package br.com.contasdomesticas.api.dto;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    String tipo,
    long expiraEmSegundos
) {
}
