package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Aporte;
import br.com.contasdomesticas.api.domain.Investimento;
import br.com.contasdomesticas.api.dto.AporteRequest;
import br.com.contasdomesticas.api.dto.AporteResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.AporteMapper;
import br.com.contasdomesticas.api.repository.AporteRepository;
import br.com.contasdomesticas.api.repository.InvestimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AporteService {

    private final AporteRepository aporteRepository;
    private final InvestimentoRepository investimentoRepository;
    private final AporteMapper aporteMapper;

    @Transactional(readOnly = true)
    public List<AporteResponse> listar(Long investimentoId) {
        exigirInvestimento(investimentoId);
        return aporteRepository.findByInvestimentoId(investimentoId).stream()
            .map(aporteMapper::toResponse).toList();
    }

    @Transactional
    public AporteResponse adicionar(Long investimentoId, AporteRequest request) {
        Investimento investimento = exigirInvestimento(investimentoId);
        Aporte aporte = new Aporte();
        aporte.setInvestimento(investimento);
        aporte.setValor(request.valor());
        aporte.setData(request.data());
        aporte.setTipo(request.tipo());
        return aporteMapper.toResponse(aporteRepository.save(aporte));
    }

    private Investimento exigirInvestimento(Long id) {
        return investimentoRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Investimento nao encontrado com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
