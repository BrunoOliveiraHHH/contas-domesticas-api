package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoMercado;

import java.time.Instant;

public record MercadoResponse(
    Long id,
    String nome,
    TipoMercado tipo,
    String endereco,
    String bairro,
    boolean ativo,
    Instant criadoEm,
    String criadoPor
) {
}
