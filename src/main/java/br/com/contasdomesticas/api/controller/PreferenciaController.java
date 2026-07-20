package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.PreferenciaRequest;
import br.com.contasdomesticas.api.dto.PreferenciaResponse;
import br.com.contasdomesticas.api.service.PreferenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/preferencias")
@RequiredArgsConstructor
public class PreferenciaController {

    private final PreferenciaService preferenciaService;

    @GetMapping("/{chave}")
    public PreferenciaResponse resolver(
        @PathVariable String chave,
        @RequestParam(required = false) Long usuarioId) {
        return preferenciaService.resolver(chave, usuarioId);
    }

    @PutMapping("/{chave}")
    public PreferenciaResponse gravar(
        @PathVariable String chave,
        @Valid @RequestBody PreferenciaRequest request) {
        return preferenciaService.gravar(chave, request);
    }
}
