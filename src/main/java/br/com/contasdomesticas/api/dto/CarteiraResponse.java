package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoCarteira;

import java.math.BigDecimal;
import java.time.Instant;

public record CarteiraResponse(
    Long id,
    String nome,
    TipoCarteira tipo,
    Long donoId,
    String donoLogin,
    BigDecimal saldoInicial,
    String moeda,
    String cor,
    String icone,
    boolean ativa,
    Instant criadoEm,
    String criadoPor
) {
}
