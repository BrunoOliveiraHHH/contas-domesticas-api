package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.domain.TipoLancamento;
import br.com.contasdomesticas.api.dto.PorCategoriaItemResponse;
import br.com.contasdomesticas.api.dto.SaldoMesResponse;
import br.com.contasdomesticas.api.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/saldo")
    public SaldoMesResponse saldo(
        @RequestParam String periodo,
        @RequestParam(required = false) Long carteira) {
        return relatorioService.saldoDoMes(periodo, carteira);
    }

    @GetMapping("/por-categoria")
    public List<PorCategoriaItemResponse> porCategoria(
        @RequestParam String periodo,
        @RequestParam(required = false) TipoLancamento tipo) {
        return relatorioService.porCategoria(periodo, tipo);
    }
}
