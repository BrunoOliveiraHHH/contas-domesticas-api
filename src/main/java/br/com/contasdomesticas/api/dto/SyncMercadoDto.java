package br.com.contasdomesticas.api.dto;

import br.com.contasdomesticas.api.domain.TipoMercado;

import java.time.Instant;
import java.util.UUID;

/**
 * Registro sincronizavel de Mercado (entidade de referencia da sincronizacao).
 * versao/deletado dirigem o merge; atualizadoEm e o carimbo do servidor (delta).
 */
public record SyncMercadoDto(
    UUID uuid,
    String nome,
    TipoMercado tipo,
    String endereco,
    String bairro,
    Boolean ativo,
    Long versao,
    boolean deletado,
    Instant atualizadoEm
) {
}
