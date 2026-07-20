package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.UnidadeMedidaRequest;
import br.com.contasdomesticas.api.dto.UnidadeMedidaResponse;
import br.com.contasdomesticas.api.service.UnidadeMedidaService;
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
@RequestMapping("/api/v1/unidades-medida")
@RequiredArgsConstructor
public class UnidadeMedidaController {

    private final UnidadeMedidaService unidadeMedidaService;

    @GetMapping
    public List<UnidadeMedidaResponse> listar() {
        return unidadeMedidaService.listar();
    }

    @GetMapping("/{id}")
    public UnidadeMedidaResponse buscar(@PathVariable Long id) {
        return unidadeMedidaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnidadeMedidaResponse criar(@Valid @RequestBody UnidadeMedidaRequest request) {
        return unidadeMedidaService.criar(request);
    }

    @PutMapping("/{id}")
    public UnidadeMedidaResponse atualizar(@PathVariable Long id, @Valid @RequestBody UnidadeMedidaRequest request) {
        return unidadeMedidaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        unidadeMedidaService.remover(id);
    }
}
