package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.CotacaoProdutoRequest;
import br.com.contasdomesticas.api.dto.CotacaoProdutoResponse;
import br.com.contasdomesticas.api.dto.ProdutoRequest;
import br.com.contasdomesticas.api.dto.ProdutoResponse;
import br.com.contasdomesticas.api.service.CotacaoProdutoService;
import br.com.contasdomesticas.api.service.ProdutoService;
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
@RequestMapping("/api/v1/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;
    private final CotacaoProdutoService cotacaoProdutoService;

    @GetMapping
    public List<ProdutoResponse> listar() {
        return produtoService.listar();
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscar(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse criar(@Valid @RequestBody ProdutoRequest request) {
        return produtoService.criar(request);
    }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request) {
        return produtoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        produtoService.remover(id);
    }

    // --- Cotacoes do produto (reutilizaveis) ---

    @GetMapping("/{id}/cotacoes")
    public List<CotacaoProdutoResponse> cotacoes(@PathVariable Long id) {
        return cotacaoProdutoService.listar(id);
    }

    @PostMapping("/{id}/cotacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public CotacaoProdutoResponse adicionarCotacao(
        @PathVariable Long id, @Valid @RequestBody CotacaoProdutoRequest request) {
        return cotacaoProdutoService.adicionar(id, request);
    }
}
