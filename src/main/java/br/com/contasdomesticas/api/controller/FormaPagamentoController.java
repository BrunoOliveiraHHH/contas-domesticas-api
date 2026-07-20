package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.FormaPagamentoRequest;
import br.com.contasdomesticas.api.dto.FormaPagamentoResponse;
import br.com.contasdomesticas.api.service.FormaPagamentoService;
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
@RequestMapping("/api/v1/formas-pagamento")
@RequiredArgsConstructor
public class FormaPagamentoController {

    private final FormaPagamentoService formaPagamentoService;

    @GetMapping
    public List<FormaPagamentoResponse> listar() {
        return formaPagamentoService.listar();
    }

    @GetMapping("/{id}")
    public FormaPagamentoResponse buscar(@PathVariable Long id) {
        return formaPagamentoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FormaPagamentoResponse criar(@Valid @RequestBody FormaPagamentoRequest request) {
        return formaPagamentoService.criar(request);
    }

    @PutMapping("/{id}")
    public FormaPagamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody FormaPagamentoRequest request) {
        return formaPagamentoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        formaPagamentoService.remover(id);
    }
}
