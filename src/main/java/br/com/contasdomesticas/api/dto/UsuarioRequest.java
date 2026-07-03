package br.com.contasdomesticas.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados de entrada para criacao/atualizacao de usuario.
 */
public record UsuarioRequest(

        @NotBlank
        @Size(max = 100)
        String login,

        @NotBlank
        @Size(max = 150)
        String nomeExibicao,

        @NotBlank
        @Size(min = 6, max = 100)
        String senha
) {
}
