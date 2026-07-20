package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.LancamentoResponse;
import br.com.contasdomesticas.api.dto.RecorrenciaRequest;
import br.com.contasdomesticas.api.dto.RecorrenciaResponse;
import br.com.contasdomesticas.api.service.RecorrenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recorrencias")
@RequiredArgsConstructor
public class RecorrenciaController {

    private final RecorrenciaService recorrenciaService;

    @GetMapping
    public List<RecorrenciaResponse> listar() {
        return recorrenciaService.listar();
    }

    @GetMapping("/{id}")
    public RecorrenciaResponse buscar(@PathVariable Long id) {
        return recorrenciaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecorrenciaResponse criar(@Valid @RequestBody RecorrenciaRequest request) {
        return recorrenciaService.criar(request);
    }

    @PutMapping("/{id}")
    public RecorrenciaResponse atualizar(@PathVariable Long id, @Valid @RequestBody RecorrenciaRequest request) {
        return recorrenciaService.atualizar(id, request);
    }

    @PostMapping("/{id}/gerar")
    public LancamentoResponse gerar(
        @PathVariable Long id,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate competencia) {
        return recorrenciaService.gerar(id, competencia);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        recorrenciaService.remover(id);
    }
}
