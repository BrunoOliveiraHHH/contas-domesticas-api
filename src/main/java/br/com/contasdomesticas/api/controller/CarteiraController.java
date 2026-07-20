package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.CarteiraRequest;
import br.com.contasdomesticas.api.dto.CarteiraResponse;
import br.com.contasdomesticas.api.service.CarteiraService;
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
@RequestMapping("/api/v1/carteiras")
@RequiredArgsConstructor
public class CarteiraController {

    private final CarteiraService carteiraService;

    @GetMapping
    public List<CarteiraResponse> listar() {
        return carteiraService.listar();
    }

    @GetMapping("/{id}")
    public CarteiraResponse buscar(@PathVariable Long id) {
        return carteiraService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarteiraResponse criar(@Valid @RequestBody CarteiraRequest request) {
        return carteiraService.criar(request);
    }

    @PutMapping("/{id}")
    public CarteiraResponse atualizar(@PathVariable Long id, @Valid @RequestBody CarteiraRequest request) {
        return carteiraService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        carteiraService.remover(id);
    }
}
