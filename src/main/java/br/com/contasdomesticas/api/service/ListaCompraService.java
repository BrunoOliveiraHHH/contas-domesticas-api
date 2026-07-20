package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Categoria;
import br.com.contasdomesticas.api.domain.CotacaoProduto;
import br.com.contasdomesticas.api.domain.ItemCompra;
import br.com.contasdomesticas.api.domain.Lancamento;
import br.com.contasdomesticas.api.domain.ListaCompra;
import br.com.contasdomesticas.api.domain.Mercado;
import br.com.contasdomesticas.api.domain.OrigemCotacao;
import br.com.contasdomesticas.api.domain.StatusLancamento;
import br.com.contasdomesticas.api.domain.StatusLista;
import br.com.contasdomesticas.api.domain.TipoCategoria;
import br.com.contasdomesticas.api.domain.TipoLancamento;
import br.com.contasdomesticas.api.dto.FecharListaRequest;
import br.com.contasdomesticas.api.dto.LancamentoResponse;
import br.com.contasdomesticas.api.dto.ListaCompraRequest;
import br.com.contasdomesticas.api.dto.ListaCompraResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.LancamentoMapper;
import br.com.contasdomesticas.api.mapper.ListaCompraMapper;
import br.com.contasdomesticas.api.repository.CarteiraRepository;
import br.com.contasdomesticas.api.repository.CategoriaRepository;
import br.com.contasdomesticas.api.repository.CotacaoProdutoRepository;
import br.com.contasdomesticas.api.repository.ItemCompraRepository;
import br.com.contasdomesticas.api.repository.LancamentoRepository;
import br.com.contasdomesticas.api.repository.ListaCompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ListaCompraService {

    private final ListaCompraRepository listaCompraRepository;
    private final CarteiraRepository carteiraRepository;
    private final ItemCompraRepository itemCompraRepository;
    private final LancamentoRepository lancamentoRepository;
    private final CotacaoProdutoRepository cotacaoProdutoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ListaCompraMapper listaCompraMapper;
    private final LancamentoMapper lancamentoMapper;

    @Transactional(readOnly = true)
    public List<ListaCompraResponse> listar(StatusLista status) {
        List<ListaCompra> listas = status != null
            ? listaCompraRepository.findByStatus(status)
            : listaCompraRepository.findAll();
        return listas.stream().map(listaCompraMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ListaCompraResponse buscarPorId(Long id) {
        return listaCompraMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ListaCompraResponse criar(ListaCompraRequest request) {
        ListaCompra lista = new ListaCompra();
        lista.setStatus(StatusLista.ABERTA);
        aplicar(lista, request);
        return listaCompraMapper.toResponse(listaCompraRepository.save(lista));
    }

    @Transactional
    public ListaCompraResponse atualizar(Long id, ListaCompraRequest request) {
        ListaCompra lista = buscarEntidade(id);
        aplicar(lista, request);
        return listaCompraMapper.toResponse(listaCompraRepository.save(lista));
    }

    @Transactional
    public void remover(Long id) {
        listaCompraRepository.delete(buscarEntidade(id));
    }

    /** Duplica a lista (nova ABERTA) copiando os itens (produto + quantidade). */
    @Transactional
    public ListaCompraResponse duplicar(Long id) {
        ListaCompra origem = buscarEntidade(id);
        ListaCompra nova = new ListaCompra();
        nova.setNome(origem.getNome() + " (copia)");
        nova.setTipo(origem.getTipo());
        nova.setCarteira(origem.getCarteira());
        nova.setData(LocalDate.now());
        nova.setStatus(StatusLista.ABERTA);
        ListaCompra salva = listaCompraRepository.save(nova);

        for (ItemCompra item : itemCompraRepository.findByListaCompraId(id)) {
            ItemCompra copia = new ItemCompra();
            copia.setListaCompra(salva);
            copia.setProduto(item.getProduto());
            copia.setQuantidade(item.getQuantidade());
            copia.setUnidadeMedida(item.getUnidadeMedida());
            itemCompraRepository.save(copia);
        }
        return listaCompraMapper.toResponse(salva);
    }

    /** Fecha a lista: agrupa os itens por estabelecimento escolhido e gera 1 despesa por loja. */
    @Transactional
    public List<LancamentoResponse> fechar(Long id, FecharListaRequest request) {
        ListaCompra lista = buscarEntidade(id);
        if (lista.getStatus() != StatusLista.ABERTA) {
            throw new AplicacaoException("A lista nao esta aberta", HttpStatus.CONFLICT);
        }
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
            .orElseThrow(() -> new AplicacaoException("Categoria informada nao existe", HttpStatus.BAD_REQUEST));
        if (categoria.getTipo() != TipoCategoria.DESPESA) {
            throw new AplicacaoException("A categoria deve ser do tipo DESPESA", HttpStatus.BAD_REQUEST);
        }

        List<ItemCompra> itens = itemCompraRepository.findByListaCompraId(id);
        if (itens.isEmpty()) {
            throw new AplicacaoException("A lista nao possui itens", HttpStatus.BAD_REQUEST);
        }

        Map<Long, Grupo> grupos = new LinkedHashMap<>();
        for (ItemCompra item : itens) {
            if (item.getMercadoEscolhido() == null || item.getPrecoUnitario() == null) {
                throw new AplicacaoException(
                    "Todos os itens precisam de estabelecimento escolhido", HttpStatus.BAD_REQUEST);
            }
            BigDecimal totalItem = item.getQuantidade()
                .multiply(item.getPrecoUnitario()).setScale(2, RoundingMode.HALF_UP);
            Grupo grupo = grupos.computeIfAbsent(item.getMercadoEscolhido().getId(),
                k -> new Grupo(item.getMercadoEscolhido()));
            grupo.total = grupo.total.add(totalItem);

            item.setComprado(true);
            itemCompraRepository.save(item);

            // registra o preco pago no catalogo (origem COMPRA)
            CotacaoProduto compra = new CotacaoProduto();
            compra.setProduto(item.getProduto());
            compra.setMercado(item.getMercadoEscolhido());
            compra.setPrecoUnitario(item.getPrecoUnitario());
            compra.setData(LocalDate.now());
            compra.setOrigem(OrigemCotacao.COMPRA);
            cotacaoProdutoRepository.save(compra);
        }

        List<Lancamento> despesas = new ArrayList<>();
        for (Grupo grupo : grupos.values()) {
            Lancamento despesa = new Lancamento();
            despesa.setTipo(TipoLancamento.DESPESA);
            despesa.setDescricao("Compra: " + lista.getNome() + " - " + grupo.mercado.getNome());
            despesa.setValor(grupo.total);
            despesa.setDataCompetencia(lista.getData());
            despesa.setDataVencimento(lista.getData());
            despesa.setStatus(StatusLancamento.PENDENTE);
            despesa.setCarteira(lista.getCarteira());
            despesa.setCategoria(categoria);
            despesas.add(lancamentoRepository.save(despesa));
        }

        lista.setStatus(StatusLista.FECHADA);
        listaCompraRepository.save(lista);
        return despesas.stream().map(lancamentoMapper::toResponse).toList();
    }

    private void aplicar(ListaCompra lista, ListaCompraRequest request) {
        lista.setNome(request.nome());
        lista.setTipo(request.tipo());
        lista.setData(request.data() != null ? request.data() : LocalDate.now());
        lista.setCarteira(carteiraRepository.findById(request.carteiraId())
            .orElseThrow(() -> new AplicacaoException("Carteira informada nao existe", HttpStatus.BAD_REQUEST)));
    }

    private ListaCompra buscarEntidade(Long id) {
        return listaCompraRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Lista de compra nao encontrada com o id: " + id, HttpStatus.NOT_FOUND));
    }

    private static final class Grupo {
        private final Mercado mercado;
        private BigDecimal total = BigDecimal.ZERO;

        private Grupo(Mercado mercado) {
            this.mercado = mercado;
        }
    }
}
