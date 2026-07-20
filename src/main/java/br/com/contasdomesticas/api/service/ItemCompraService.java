package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.CotacaoProduto;
import br.com.contasdomesticas.api.domain.ItemCompra;
import br.com.contasdomesticas.api.domain.ListaCompra;
import br.com.contasdomesticas.api.domain.Mercado;
import br.com.contasdomesticas.api.domain.Produto;
import br.com.contasdomesticas.api.dto.EscolhaEstabelecimentoRequest;
import br.com.contasdomesticas.api.dto.ItemCompraRequest;
import br.com.contasdomesticas.api.dto.ItemCompraResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.ItemCompraMapper;
import br.com.contasdomesticas.api.repository.CotacaoProdutoRepository;
import br.com.contasdomesticas.api.repository.ItemCompraRepository;
import br.com.contasdomesticas.api.repository.ListaCompraRepository;
import br.com.contasdomesticas.api.repository.MercadoRepository;
import br.com.contasdomesticas.api.repository.ProdutoRepository;
import br.com.contasdomesticas.api.repository.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemCompraService {

    private final ItemCompraRepository itemCompraRepository;
    private final ListaCompraRepository listaCompraRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final MercadoRepository mercadoRepository;
    private final CotacaoProdutoRepository cotacaoProdutoRepository;
    private final ItemCompraMapper itemCompraMapper;

    @Transactional(readOnly = true)
    public List<ItemCompraResponse> listar(Long listaId) {
        exigirLista(listaId);
        return itemCompraRepository.findByListaCompraId(listaId).stream()
            .map(itemCompraMapper::toResponse).toList();
    }

    @Transactional
    public ItemCompraResponse adicionar(Long listaId, ItemCompraRequest request) {
        ListaCompra lista = exigirLista(listaId);
        Produto produto = produtoRepository.findById(request.produtoId())
            .orElseThrow(() -> new AplicacaoException("Produto informado nao existe", HttpStatus.BAD_REQUEST));

        ItemCompra item = new ItemCompra();
        item.setListaCompra(lista);
        item.setProduto(produto);
        item.setQuantidade(request.quantidade());
        if (request.unidadeMedidaId() != null) {
            item.setUnidadeMedida(unidadeMedidaRepository.findById(request.unidadeMedidaId())
                .orElseThrow(() -> new AplicacaoException("Unidade informada nao existe", HttpStatus.BAD_REQUEST)));
        } else {
            item.setUnidadeMedida(produto.getUnidadeMedidaPadrao());
        }
        return itemCompraMapper.toResponse(itemCompraRepository.save(item));
    }

    @Transactional
    public ItemCompraResponse escolherEstabelecimento(Long itemId, EscolhaEstabelecimentoRequest request) {
        ItemCompra item = buscarItem(itemId);
        Mercado mercado = mercadoRepository.findById(request.mercadoId())
            .orElseThrow(() -> new AplicacaoException("Mercado informado nao existe", HttpStatus.BAD_REQUEST));

        CotacaoProduto cotacao = cotacaoProdutoRepository
            .findFirstByProdutoIdAndMercadoIdOrderByDataDesc(item.getProduto().getId(), mercado.getId())
            .orElseThrow(() -> new AplicacaoException(
                "Nao ha cotacao deste produto no estabelecimento informado", HttpStatus.BAD_REQUEST));

        item.setMercadoEscolhido(mercado);
        item.setPrecoUnitario(cotacao.getPrecoUnitario());
        return itemCompraMapper.toResponse(itemCompraRepository.save(item));
    }

    @Transactional
    public void remover(Long itemId) {
        itemCompraRepository.delete(buscarItem(itemId));
    }

    private ItemCompra buscarItem(Long id) {
        return itemCompraRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Item de compra nao encontrado com o id: " + id, HttpStatus.NOT_FOUND));
    }

    private ListaCompra exigirLista(Long id) {
        return listaCompraRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Lista de compra nao encontrada com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
