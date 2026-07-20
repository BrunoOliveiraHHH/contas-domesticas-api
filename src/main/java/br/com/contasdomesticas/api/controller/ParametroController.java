package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.ImpostoIrResponse;
import br.com.contasdomesticas.api.dto.ParametroRequest;
import br.com.contasdomesticas.api.dto.ParametroResponse;
import br.com.contasdomesticas.api.service.ImpostoService;
import br.com.contasdomesticas.api.service.ParametroService;
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
@RequestMapping("/api/v1/parametros")
@RequiredArgsConstructor
public class ParametroController {

    private final ParametroService parametroService;
    private final ImpostoService impostoService;

    @GetMapping
    public List<ParametroResponse> listar() {
        return parametroService.listar();
    }

    @GetMapping("/vigente/{chave}")
    public ParametroResponse vigente(
        @PathVariable String chave,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return parametroService.buscarVigente(chave, data);
    }

    @GetMapping("/imposto-ir")
    public ImpostoIrResponse impostoIr(
        @RequestParam int dias,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return impostoService.resolverIr(dias, data);
    }

    @GetMapping("/{id}")
    public ParametroResponse buscar(@PathVariable Long id) {
        return parametroService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParametroResponse criar(@Valid @RequestBody ParametroRequest request) {
        return parametroService.criar(request);
    }

    @PutMapping("/{id}")
    public ParametroResponse atualizar(@PathVariable Long id, @Valid @RequestBody ParametroRequest request) {
        return parametroService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        parametroService.remover(id);
    }
}
