package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.SyncMercadoDto;
import br.com.contasdomesticas.api.service.SincronizacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SincronizacaoController {

    private final SincronizacaoService sincronizacaoService;

    @GetMapping("/mercados")
    public List<SyncMercadoDto> delta(@RequestParam(required = false) Instant desde) {
        return sincronizacaoService.delta(desde);
    }

    @PostMapping("/mercados")
    public List<SyncMercadoDto> merge(@RequestBody List<SyncMercadoDto> registros) {
        return sincronizacaoService.merge(registros);
    }
}
