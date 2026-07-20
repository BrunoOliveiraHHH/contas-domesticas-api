package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.AporteRequest;
import br.com.contasdomesticas.api.dto.AporteResponse;
import br.com.contasdomesticas.api.dto.InvestimentoRequest;
import br.com.contasdomesticas.api.dto.InvestimentoResponse;
import br.com.contasdomesticas.api.dto.PatrimonioResponse;
import br.com.contasdomesticas.api.dto.SaldoInvestimentoResponse;
import br.com.contasdomesticas.api.service.AporteService;
import br.com.contasdomesticas.api.service.InvestimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/investimentos")
@RequiredArgsConstructor
public class InvestimentoController {

    private final InvestimentoService investimentoService;
    private final AporteService aporteService;

    @GetMapping
    public List<InvestimentoResponse> listar() {
        return investimentoService.listar();
    }

    @GetMapping("/patrimonio")
    public PatrimonioResponse patrimonio() {
        return investimentoService.patrimonio();
    }

    @GetMapping("/{id}")
    public InvestimentoResponse buscar(@PathVariable Long id) {
        return investimentoService.buscarPorId(id);
    }

    @GetMapping("/{id}/saldo")
    public SaldoInvestimentoResponse saldo(@PathVariable Long id) {
        return investimentoService.saldo(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvestimentoResponse criar(@Valid @RequestBody InvestimentoRequest request) {
        return investimentoService.criar(request);
    }

    @PutMapping("/{id}")
    public InvestimentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody InvestimentoRequest request) {
        return investimentoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        investimentoService.remover(id);
    }

    // --- Aportes / resgates ---

    @GetMapping("/{id}/aportes")
    public List<AporteResponse> aportes(@PathVariable Long id) {
        return aporteService.listar(id);
    }

    @PostMapping("/{id}/aportes")
    @ResponseStatus(HttpStatus.CREATED)
    public AporteResponse adicionarAporte(@PathVariable Long id, @Valid @RequestBody AporteRequest request) {
        return aporteService.adicionar(id, request);
    }
}
