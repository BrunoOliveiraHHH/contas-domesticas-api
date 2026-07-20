package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.CotacaoProduto;
import br.com.contasdomesticas.api.domain.Mercado;
import br.com.contasdomesticas.api.domain.OrigemCotacao;
import br.com.contasdomesticas.api.domain.Produto;
import br.com.contasdomesticas.api.dto.CotacaoProdutoRequest;
import br.com.contasdomesticas.api.dto.CotacaoProdutoResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.CotacaoProdutoMapper;
import br.com.contasdomesticas.api.repository.CotacaoProdutoRepository;
import br.com.contasdomesticas.api.repository.MercadoRepository;
import br.com.contasdomesticas.api.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CotacaoProdutoService {

    private final CotacaoProdutoRepository cotacaoProdutoRepository;
    private final ProdutoRepository produtoRepository;
    private final MercadoRepository mercadoRepository;
    private final CotacaoProdutoMapper cotacaoProdutoMapper;

    @Transactional(readOnly = true)
    public List<CotacaoProdutoResponse> listar(Long produtoId) {
        exigirProduto(produtoId);
        return cotacaoProdutoRepository.findByProdutoIdOrderByPrecoUnitarioAsc(produtoId).stream()
            .map(cotacaoProdutoMapper::toResponse).toList();
    }

    @Transactional
    public CotacaoProdutoResponse adicionar(Long produtoId, CotacaoProdutoRequest request) {
        Produto produto = exigirProduto(produtoId);
        Mercado mercado = mercadoRepository.findById(request.mercadoId())
            .orElseThrow(() -> new AplicacaoException("Mercado informado nao existe", HttpStatus.BAD_REQUEST));

        CotacaoProduto cotacao = new CotacaoProduto();
        cotacao.setProduto(produto);
        cotacao.setMercado(mercado);
        cotacao.setPrecoUnitario(request.precoUnitario());
        cotacao.setData(request.data() != null ? request.data() : LocalDate.now());
        cotacao.setOrigem(OrigemCotacao.COTACAO);
        return cotacaoProdutoMapper.toResponse(cotacaoProdutoRepository.save(cotacao));
    }

    private Produto exigirProduto(Long produtoId) {
        return produtoRepository.findById(produtoId)
            .orElseThrow(() -> new AplicacaoException(
                "Produto nao encontrado com o id: " + produtoId, HttpStatus.NOT_FOUND));
    }
}
