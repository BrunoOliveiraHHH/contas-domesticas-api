package br.com.contasdomesticas.api.dto;

import java.time.LocalDate;

public record PagamentoRequest(
    LocalDate dataPagamento
) {
}
