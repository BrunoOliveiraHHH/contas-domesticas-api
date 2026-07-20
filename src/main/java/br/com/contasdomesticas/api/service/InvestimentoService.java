package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Aporte;
import br.com.contasdomesticas.api.domain.Investimento;
import br.com.contasdomesticas.api.domain.TipoAporte;
import br.com.contasdomesticas.api.dto.InvestimentoRequest;
import br.com.contasdomesticas.api.dto.InvestimentoResponse;
import br.com.contasdomesticas.api.dto.PatrimonioResponse;
import br.com.contasdomesticas.api.dto.SaldoInvestimentoResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.InvestimentoMapper;
import br.com.contasdomesticas.api.repository.AporteRepository;
import br.com.contasdomesticas.api.repository.CarteiraRepository;
import br.com.contasdomesticas.api.repository.InvestimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestimentoService {

    private final InvestimentoRepository investimentoRepository;
    private final AporteRepository aporteRepository;
    private final CarteiraRepository carteiraRepository;
    private final InvestimentoMapper investimentoMapper;

    @Transactional(readOnly = true)
    public List<InvestimentoResponse> listar() {
        return investimentoRepository.findAll().stream().map(investimentoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public InvestimentoResponse buscarPorId(Long id) {
        return investimentoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public SaldoInvestimentoResponse saldo(Long id) {
        Investimento investimento = buscarEntidade(id);
        return new SaldoInvestimentoResponse(id, investimento.getNome(), saldoAplicado(id));
    }

    @Transactional(readOnly = true)
    public PatrimonioResponse patrimonio() {
        BigDecimal total = investimentoRepository.findAll().stream()
            .map(inv -> saldoAplicado(inv.getId()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PatrimonioResponse(total.setScale(2, RoundingMode.HALF_UP));
    }

    @Transactional
    public InvestimentoResponse criar(InvestimentoRequest request) {
        Investimento investimento = new Investimento();
        aplicar(investimento, request);
        return investimentoMapper.toResponse(investimentoRepository.save(investimento));
    }

    @Transactional
    public InvestimentoResponse atualizar(Long id, InvestimentoRequest request) {
        Investimento investimento = buscarEntidade(id);
        aplicar(investimento, request);
        return investimentoMapper.toResponse(investimentoRepository.save(investimento));
    }

    @Transactional
    public void remover(Long id) {
        investimentoRepository.delete(buscarEntidade(id));
    }

    private BigDecimal saldoAplicado(Long investimentoId) {
        BigDecimal saldo = BigDecimal.ZERO;
        for (Aporte aporte : aporteRepository.findByInvestimentoId(investimentoId)) {
            saldo = aporte.getTipo() == TipoAporte.APORTE
                ? saldo.add(aporte.getValor())
                : saldo.subtract(aporte.getValor());
        }
        return saldo.setScale(2, RoundingMode.HALF_UP);
    }

    private void aplicar(Investimento investimento, InvestimentoRequest request) {
        investimento.setNome(request.nome());
        investimento.setTipoInvestimento(request.tipoInvestimento());
        investimento.setInstituicao(request.instituicao());
        investimento.setIndexador(request.indexador());
        investimento.setTaxaContratada(request.taxaContratada());
        investimento.setDataAplicacao(request.dataAplicacao());
        investimento.setDataVencimento(request.dataVencimento());
        investimento.setCarteira(carteiraRepository.findById(request.carteiraId())
            .orElseThrow(() -> new AplicacaoException("Carteira informada nao existe", HttpStatus.BAD_REQUEST)));
    }

    private Investimento buscarEntidade(Long id) {
        return investimentoRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Investimento nao encontrado com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
