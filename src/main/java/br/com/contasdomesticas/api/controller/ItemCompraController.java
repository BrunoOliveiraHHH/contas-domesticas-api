package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.EscolhaEstabelecimentoRequest;
import br.com.contasdomesticas.api.dto.ItemCompraRequest;
import br.com.contasdomesticas.api.dto.ItemCompraResponse;
import br.com.contasdomesticas.api.service.ItemCompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemCompraController {

    private final ItemCompraService itemCompraService;

    @GetMapping("/api/v1/listas-compra/{listaId}/itens")
    public List<ItemCompraResponse> listar(@PathVariable Long listaId) {
        return itemCompraService.listar(listaId);
    }

    @PostMapping("/api/v1/listas-compra/{listaId}/itens")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemCompraResponse adicionar(@PathVariable Long listaId, @Valid @RequestBody ItemCompraRequest request) {
        return itemCompraService.adicionar(listaId, request);
    }

    @PostMapping("/api/v1/listas-compra/{listaId}/repor-estoque")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ItemCompraResponse> reporEstoque(@PathVariable Long listaId) {
        return itemCompraService.reporEstoque(listaId);
    }

    @PutMapping("/api/v1/itens/{id}/escolha")
    public ItemCompraResponse escolher(@PathVariable Long id, @Valid @RequestBody EscolhaEstabelecimentoRequest request) {
        return itemCompraService.escolherEstabelecimento(id, request);
    }

    @DeleteMapping("/api/v1/itens/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        itemCompraService.remover(id);
    }
}
