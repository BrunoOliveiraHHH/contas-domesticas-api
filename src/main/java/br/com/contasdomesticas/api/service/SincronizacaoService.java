package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Mercado;
import br.com.contasdomesticas.api.dto.SyncMercadoDto;
import br.com.contasdomesticas.api.repository.MercadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Sincronizacao entre instancias. Reference implementation sobre Mercado:
 * - delta (pull): registros alterados desde um instante (inclui tombstones).
 * - merge (push): upsert por uuid com last-write-wins pela maior versao.
 */
@Service
@RequiredArgsConstructor
public class SincronizacaoService {

    private final MercadoRepository mercadoRepository;

    @Transactional(readOnly = true)
    public List<SyncMercadoDto> delta(Instant desde) {
        List<Mercado> mercados = desde != null
            ? mercadoRepository.findByAtualizadoEmGreaterThanEqual(desde)
            : mercadoRepository.findAll();
        return mercados.stream().map(this::toDto).toList();
    }

    @Transactional
    public List<SyncMercadoDto> merge(List<SyncMercadoDto> registros) {
        List<Mercado> afetados = new ArrayList<>();
        for (SyncMercadoDto dto : registros) {
            Mercado mercado = mercadoRepository.findByUuid(dto.uuid()).orElse(null);
            if (mercado == null) {
                mercado = new Mercado();
                mercado.setUuid(dto.uuid());
                aplicar(mercado, dto);
                afetados.add(mercadoRepository.save(mercado));
            } else if (venceConflito(dto, mercado)) {
                aplicar(mercado, dto);
                afetados.add(mercadoRepository.save(mercado));
            } else {
                // registro recebido e mais antigo: ignora (mantem o do servidor)
                afetados.add(mercado);
            }
        }
        return afetados.stream().map(this::toDto).toList();
    }

    // last-write-wins pela maior versao
    private boolean venceConflito(SyncMercadoDto dto, Mercado mercado) {
        long recebida = dto.versao() != null ? dto.versao() : 0L;
        long atual = mercado.getVersao() != null ? mercado.getVersao() : 0L;
        return recebida > atual;
    }

    private void aplicar(Mercado mercado, SyncMercadoDto dto) {
        mercado.setNome(dto.nome());
        mercado.setTipo(dto.tipo());
        mercado.setEndereco(dto.endereco());
        mercado.setBairro(dto.bairro());
        mercado.setAtivo(dto.ativo() == null || dto.ativo());
        mercado.setVersao(dto.versao() != null ? dto.versao() : 0L);
        mercado.setDeletado(dto.deletado());
    }

    private SyncMercadoDto toDto(Mercado m) {
        return new SyncMercadoDto(m.getUuid(), m.getNome(), m.getTipo(), m.getEndereco(),
            m.getBairro(), m.isAtivo(), m.getVersao(), m.isDeletado(), m.getAtualizadoEm());
    }
}
