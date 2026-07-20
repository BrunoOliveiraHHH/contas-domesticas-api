package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.LancamentoResponse;
import br.com.contasdomesticas.api.dto.ReceitaRequest;
import br.com.contasdomesticas.api.service.ReceitaService;
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
@RequestMapping("/api/v1/receitas")
@RequiredArgsConstructor
public class ReceitaController {

    private final ReceitaService receitaService;

    @GetMapping
    public List<LancamentoResponse> listar() {
        return receitaService.listar();
    }

    @GetMapping("/{id}")
    public LancamentoResponse buscar(@PathVariable Long id) {
        return receitaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LancamentoResponse criar(@Valid @RequestBody ReceitaRequest request) {
        return receitaService.criar(request);
    }

    @PutMapping("/{id}")
    public LancamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ReceitaRequest request) {
        return receitaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        receitaService.remover(id);
    }
}
