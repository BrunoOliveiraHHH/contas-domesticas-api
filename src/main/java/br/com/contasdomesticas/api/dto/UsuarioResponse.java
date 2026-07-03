package br.com.contasdomesticas.api.dto;

import java.time.Instant;

/**
 * Representacao de saida do usuario (nunca expoe a senha).
 */
public record UsuarioResponse(
        Long id,
        String login,
        String nomeExibicao,
        Instant criadoEm,
        String criadoPor,
        Instant atualizadoEm,
        String atualizadoPor
) {
}
