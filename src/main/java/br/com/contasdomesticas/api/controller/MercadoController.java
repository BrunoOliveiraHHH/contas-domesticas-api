package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.MercadoRequest;
import br.com.contasdomesticas.api.dto.MercadoResponse;
import br.com.contasdomesticas.api.service.MercadoService;
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
@RequestMapping("/api/v1/mercados")
@RequiredArgsConstructor
public class MercadoController {

    private final MercadoService mercadoService;

    @GetMapping
    public List<MercadoResponse> listar() {
        return mercadoService.listar();
    }

    @GetMapping("/{id}")
    public MercadoResponse buscar(@PathVariable Long id) {
        return mercadoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MercadoResponse criar(@Valid @RequestBody MercadoRequest request) {
        return mercadoService.criar(request);
    }

    @PutMapping("/{id}")
    public MercadoResponse atualizar(@PathVariable Long id, @Valid @RequestBody MercadoRequest request) {
        return mercadoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        mercadoService.remover(id);
    }
}
