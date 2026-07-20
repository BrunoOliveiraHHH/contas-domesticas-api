package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.StatusLista;
import br.com.contasdomesticas.api.domain.TipoLista;

import java.time.Instant;
import java.time.LocalDate;

public record ListaCompraResponse(
    Long id,
    String nome,
    TipoLista tipo,
    Long carteiraId,
    LocalDate data,
    StatusLista status,
    Instant criadoEm,
    String criadoPor
) {
}
