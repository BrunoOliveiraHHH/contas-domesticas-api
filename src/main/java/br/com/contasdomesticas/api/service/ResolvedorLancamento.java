package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Carteira;
import br.com.contasdomesticas.api.domain.Categoria;
import br.com.contasdomesticas.api.domain.FormaPagamento;
import br.com.contasdomesticas.api.domain.TipoCategoria;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.repository.CarteiraRepository;
import br.com.contasdomesticas.api.repository.CategoriaRepository;
import br.com.contasdomesticas.api.repository.FormaPagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Resolve e valida as referencias (carteira, categoria, forma de pagamento)
 * usadas pelos lancamentos, reaproveitado por receita e despesa.
 */
@Component
@RequiredArgsConstructor
public class ResolvedorLancamento {

    private final CarteiraRepository carteiraRepository;
    private final CategoriaRepository categoriaRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;

    public Carteira carteira(Long id) {
        return carteiraRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Carteira informada nao existe", HttpStatus.BAD_REQUEST));
    }

    public Categoria categoria(Long id, TipoCategoria tipoEsperado) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Categoria informada nao existe", HttpStatus.BAD_REQUEST));
        if (categoria.getTipo() != tipoEsperado) {
            throw new AplicacaoException(
                "A categoria deve ser do tipo " + tipoEsperado, HttpStatus.BAD_REQUEST);
        }
        return categoria;
    }

    public FormaPagamento formaPagamento(Long id) {
        if (id == null) {
            return null;
        }
        return formaPagamentoRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Forma de pagamento informada nao existe", HttpStatus.BAD_REQUEST));
    }
}
