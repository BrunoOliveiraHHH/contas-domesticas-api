package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.dto.ImpostoIrResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resolve a aliquota de IR regressivo por prazo (dias), lendo os parametros
 * vigentes (IR_ATE_180, IR_181_360, IR_361_720, IR_ACIMA_720).
 */
@Service
@RequiredArgsConstructor
public class ImpostoService {

    private final ParametroService parametroService;

    public ImpostoIrResponse resolverIr(int dias, LocalDate data) {
        String chave = chaveIrPorDias(dias);
        BigDecimal aliquota = parametroService.valorVigente(chave, data);
        return new ImpostoIrResponse(dias, chave, aliquota);
    }

    private String chaveIrPorDias(int dias) {
        if (dias <= 180) {
            return "IR_ATE_180";
        }
        if (dias <= 360) {
            return "IR_181_360";
        }
        if (dias <= 720) {
            return "IR_361_720";
        }
        return "IR_ACIMA_720";
    }
}
