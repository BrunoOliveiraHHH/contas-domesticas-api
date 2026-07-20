package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.DespesaRequest;
import br.com.contasdomesticas.api.dto.LancamentoResponse;
import br.com.contasdomesticas.api.dto.PagamentoRequest;
import br.com.contasdomesticas.api.dto.ParcelamentoRequest;
import br.com.contasdomesticas.api.service.DespesaService;
import br.com.contasdomesticas.api.service.ParcelamentoService;
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
@RequestMapping("/api/v1/despesas")
@RequiredArgsConstructor
public class DespesaController {

    private final DespesaService despesaService;
    private final ParcelamentoService parcelamentoService;

    @GetMapping
    public List<LancamentoResponse> listar() {
        return despesaService.listar();
    }

    @PostMapping("/parceladas")
    @ResponseStatus(HttpStatus.CREATED)
    public List<LancamentoResponse> parcelar(@Valid @RequestBody ParcelamentoRequest request) {
        return parcelamentoService.gerarParceladas(request);
    }

    @GetMapping("/{id}")
    public LancamentoResponse buscar(@PathVariable Long id) {
        return despesaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LancamentoResponse criar(@Valid @RequestBody DespesaRequest request) {
        return despesaService.criar(request);
    }

    @PutMapping("/{id}")
    public LancamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody DespesaRequest request) {
        return despesaService.atualizar(id, request);
    }

    @PostMapping("/{id}/pagar")
    public LancamentoResponse pagar(@PathVariable Long id, @RequestBody(required = false) PagamentoRequest request) {
        return despesaService.marcarComoPago(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        despesaService.remover(id);
    }
}
