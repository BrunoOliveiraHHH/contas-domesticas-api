package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.domain.StatusLista;
import br.com.contasdomesticas.api.dto.ListaCompraRequest;
import br.com.contasdomesticas.api.dto.ListaCompraResponse;
import br.com.contasdomesticas.api.service.ListaCompraService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/listas-compra")
@RequiredArgsConstructor
public class ListaCompraController {

    private final ListaCompraService listaCompraService;

    @GetMapping
    public List<ListaCompraResponse> listar(@RequestParam(required = false) StatusLista status) {
        return listaCompraService.listar(status);
    }

    @GetMapping("/{id}")
    public ListaCompraResponse buscar(@PathVariable Long id) {
        return listaCompraService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListaCompraResponse criar(@Valid @RequestBody ListaCompraRequest request) {
        return listaCompraService.criar(request);
    }

    @PutMapping("/{id}")
    public ListaCompraResponse atualizar(@PathVariable Long id, @Valid @RequestBody ListaCompraRequest request) {
        return listaCompraService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        listaCompraService.remover(id);
    }
}
