package br.com.contasdomesticas.api.controller;

import br.com.contasdomesticas.api.dto.AcertoItemResponse;
import br.com.contasdomesticas.api.dto.RateioRequest;
import br.com.contasdomesticas.api.dto.RateioResponse;
import br.com.contasdomesticas.api.service.RateioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RateioController {

    private final RateioService rateioService;

    @PostMapping("/api/v1/despesas/{id}/rateio")
    @ResponseStatus(HttpStatus.CREATED)
    public RateioResponse criar(@PathVariable Long id, @Valid @RequestBody RateioRequest request) {
        return rateioService.criar(id, request);
    }

    @GetMapping("/api/v1/rateios/acerto")
    public List<AcertoItemResponse> acerto(@RequestParam String periodo) {
        return rateioService.acerto(periodo);
    }
}
