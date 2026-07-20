package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Lancamento;
import br.com.contasdomesticas.api.domain.ParticipanteRateio;
import br.com.contasdomesticas.api.domain.Rateio;
import br.com.contasdomesticas.api.domain.TipoLancamento;
import br.com.contasdomesticas.api.domain.TipoRateio;
import br.com.contasdomesticas.api.domain.Usuario;
import br.com.contasdomesticas.api.dto.AcertoItemResponse;
import br.com.contasdomesticas.api.dto.RateioRequest;
import br.com.contasdomesticas.api.dto.RateioResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.repository.LancamentoRepository;
import br.com.contasdomesticas.api.repository.RateioRepository;
import br.com.contasdomesticas.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RateioService {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final RateioRepository rateioRepository;
    private final LancamentoRepository lancamentoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public RateioResponse criar(Long despesaId, RateioRequest request) {
        Lancamento despesa = lancamentoRepository.findById(despesaId)
            .filter(l -> l.getTipo() == TipoLancamento.DESPESA)
            .orElseThrow(() -> new AplicacaoException(
                "Despesa nao encontrada com o id: " + despesaId, HttpStatus.NOT_FOUND));

        if (rateioRepository.findByLancamentoId(despesaId).isPresent()) {
            throw new AplicacaoException("Esta despesa ja possui rateio", HttpStatus.CONFLICT);
        }

        List<RateioRequest.Participante> itens = request.participantes();
        int n = itens.size();
        List<BigDecimal> percentuais = calcularPercentuais(request.tipo(), itens, n);

        BigDecimal total = despesa.getValor();
        Rateio rateio = new Rateio();
        rateio.setLancamento(despesa);
        rateio.setTipo(request.tipo());

        BigDecimal somaValores = BigDecimal.ZERO;
        for (int i = 0; i < n; i++) {
            Usuario usuario = usuarioRepository.findById(itens.get(i).usuarioId())
                .orElseThrow(() -> new AplicacaoException(
                    "Usuario informado nao existe", HttpStatus.BAD_REQUEST));
            BigDecimal pct = percentuais.get(i);
            BigDecimal valor = (i < n - 1)
                ? total.multiply(pct).divide(CEM, 2, RoundingMode.HALF_UP)
                : total.subtract(somaValores);
            if (i < n - 1) {
                somaValores = somaValores.add(valor);
            }

            ParticipanteRateio participante = new ParticipanteRateio();
            participante.setUsuario(usuario);
            participante.setPercentual(pct);
            participante.setValor(valor);
            rateio.adicionar(participante);
        }

        return toResponse(rateioRepository.save(rateio));
    }

    @Transactional(readOnly = true)
    public List<AcertoItemResponse> acerto(String periodo) {
        YearMonth ym;
        try {
            ym = YearMonth.parse(periodo);
        } catch (RuntimeException e) {
            throw new AplicacaoException("Periodo invalido (use YYYY-MM)", HttpStatus.BAD_REQUEST);
        }
        List<Rateio> rateios = rateioRepository
            .findByLancamento_DataCompetenciaBetween(ym.atDay(1), ym.atEndOfMonth());

        Map<Long, Acumulado> porUsuario = new LinkedHashMap<>();
        for (Rateio rateio : rateios) {
            for (ParticipanteRateio participante : rateio.getParticipantes()) {
                Usuario usuario = participante.getUsuario();
                Acumulado acc = porUsuario.computeIfAbsent(
                    usuario.getId(), k -> new Acumulado(usuario.getLogin()));
                acc.total = acc.total.add(participante.getValor());
            }
        }
        return porUsuario.entrySet().stream()
            .map(e -> new AcertoItemResponse(e.getKey(), e.getValue().login, e.getValue().total))
            .toList();
    }

    private List<BigDecimal> calcularPercentuais(TipoRateio tipo, List<RateioRequest.Participante> itens, int n) {
        if (tipo == TipoRateio.IGUAL) {
            BigDecimal base = CEM.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
            List<BigDecimal> percentuais = new ArrayList<>();
            BigDecimal soma = BigDecimal.ZERO;
            for (int i = 0; i < n; i++) {
                BigDecimal pct = (i < n - 1) ? base : CEM.subtract(soma);
                if (i < n - 1) {
                    soma = soma.add(base);
                }
                percentuais.add(pct);
            }
            return percentuais;
        }

        // PROPORCIONAL / CUSTOM: usa os percentuais informados; a soma deve ser 100.
        List<BigDecimal> percentuais = new ArrayList<>();
        BigDecimal soma = BigDecimal.ZERO;
        for (RateioRequest.Participante item : itens) {
            if (item.percentual() == null) {
                throw new AplicacaoException(
                    "Percentual e obrigatorio para o tipo " + tipo, HttpStatus.BAD_REQUEST);
            }
            percentuais.add(item.percentual());
            soma = soma.add(item.percentual());
        }
        if (soma.compareTo(CEM) != 0) {
            throw new AplicacaoException("A soma dos percentuais deve ser 100", HttpStatus.BAD_REQUEST);
        }
        return percentuais;
    }

    private RateioResponse toResponse(Rateio rateio) {
        List<RateioResponse.Participante> participantes = rateio.getParticipantes().stream()
            .map(p -> new RateioResponse.Participante(
                p.getUsuario().getId(), p.getUsuario().getLogin(), p.getPercentual(), p.getValor()))
            .toList();
        return new RateioResponse(
            rateio.getId(), rateio.getLancamento().getId(), rateio.getTipo(), participantes);
    }

    private static final class Acumulado {
        private final String login;
        private BigDecimal total = BigDecimal.ZERO;

        private Acumulado(String login) {
            this.login = login;
        }
    }
}
