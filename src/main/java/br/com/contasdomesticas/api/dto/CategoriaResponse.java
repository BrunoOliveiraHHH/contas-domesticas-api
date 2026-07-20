package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoCategoria;

import java.time.Instant;

public record CategoriaResponse(
    Long id,
    String nome,
    TipoCategoria tipo,
    Long categoriaPaiId,
    String cor,
    String icone,
    boolean ativa,
    Instant criadoEm,
    String criadoPor
) {
}
