package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoFormaPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FormaPagamentoRequest(
    @NotBlank String nome,
    @NotNull TipoFormaPagamento tipo,
    Long carteiraId,
    Integer diaFechamento,
    Integer diaVencimento,
    Boolean ativa
) {
}
