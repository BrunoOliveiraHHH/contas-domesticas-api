package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Produto;
import br.com.contasdomesticas.api.dto.ProdutoRequest;
import br.com.contasdomesticas.api.dto.ProdutoResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.ProdutoMapper;
import br.com.contasdomesticas.api.repository.CategoriaRepository;
import br.com.contasdomesticas.api.repository.ProdutoRepository;
import br.com.contasdomesticas.api.repository.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final ProdutoMapper produtoMapper;

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return produtoRepository.findAll().stream().map(produtoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return produtoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Produto produto = new Produto();
        aplicar(produto, request);
        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidade(id);
        aplicar(produto, request);
        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public void remover(Long id) {
        produtoRepository.delete(buscarEntidade(id));
    }

    private void aplicar(Produto produto, ProdutoRequest request) {
        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setCodigoBarras(request.codigoBarras());
        produto.setAtivo(request.ativo() == null || request.ativo());
        produto.setCategoria(request.categoriaId() == null ? null
            : categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new AplicacaoException("Categoria informada nao existe", HttpStatus.BAD_REQUEST)));
        produto.setUnidadeMedidaPadrao(request.unidadeMedidaPadraoId() == null ? null
            : unidadeMedidaRepository.findById(request.unidadeMedidaPadraoId())
                .orElseThrow(() -> new AplicacaoException("Unidade de medida informada nao existe", HttpStatus.BAD_REQUEST)));
        if (request.estoqueMinimo() != null) {
            produto.setEstoqueMinimo(request.estoqueMinimo());
        }
        if (request.estoqueAtual() != null) {
            produto.setEstoqueAtual(request.estoqueAtual());
        }
    }

    private Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Produto nao encontrado com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
