package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Carteira;
import br.com.contasdomesticas.api.domain.FormaPagamento;
import br.com.contasdomesticas.api.domain.TipoFormaPagamento;
import br.com.contasdomesticas.api.dto.FormaPagamentoRequest;
import br.com.contasdomesticas.api.dto.FormaPagamentoResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.FormaPagamentoMapper;
import br.com.contasdomesticas.api.repository.CarteiraRepository;
import br.com.contasdomesticas.api.repository.FormaPagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormaPagamentoService {

    private final FormaPagamentoRepository formaPagamentoRepository;
    private final CarteiraRepository carteiraRepository;
    private final FormaPagamentoMapper formaPagamentoMapper;

    @Transactional(readOnly = true)
    public List<FormaPagamentoResponse> listar() {
        return formaPagamentoRepository.findAll().stream().map(formaPagamentoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public FormaPagamentoResponse buscarPorId(Long id) {
        return formaPagamentoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public FormaPagamentoResponse criar(FormaPagamentoRequest request) {
        FormaPagamento forma = new FormaPagamento();
        aplicar(forma, request);
        return formaPagamentoMapper.toResponse(formaPagamentoRepository.save(forma));
    }

    @Transactional
    public FormaPagamentoResponse atualizar(Long id, FormaPagamentoRequest request) {
        FormaPagamento forma = buscarEntidade(id);
        aplicar(forma, request);
        return formaPagamentoMapper.toResponse(formaPagamentoRepository.save(forma));
    }

    @Transactional
    public void remover(Long id) {
        formaPagamentoRepository.delete(buscarEntidade(id));
    }

    private void aplicar(FormaPagamento forma, FormaPagamentoRequest request) {
        forma.setNome(request.nome());
        forma.setTipo(request.tipo());
        forma.setAtiva(request.ativa() == null || request.ativa());

        if (request.carteiraId() != null) {
            Carteira carteira = carteiraRepository.findById(request.carteiraId())
                .orElseThrow(() -> new AplicacaoException(
                    "Carteira informada nao existe", HttpStatus.BAD_REQUEST));
            forma.setCarteira(carteira);
        } else {
            forma.setCarteira(null);
        }

        if (request.tipo() == TipoFormaPagamento.CREDITO) {
            if (request.diaFechamento() == null || request.diaVencimento() == null) {
                throw new AplicacaoException(
                    "Cartao de credito exige dia de fechamento e vencimento", HttpStatus.BAD_REQUEST);
            }
            forma.setDiaFechamento(request.diaFechamento());
            forma.setDiaVencimento(request.diaVencimento());
        } else {
            // Dias so fazem sentido no credito; ignorados nos demais tipos.
            forma.setDiaFechamento(null);
            forma.setDiaVencimento(null);
        }
    }

    private FormaPagamento buscarEntidade(Long id) {
        return formaPagamentoRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Forma de pagamento nao encontrada com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
