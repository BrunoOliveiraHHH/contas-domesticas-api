package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Categoria;
import br.com.contasdomesticas.api.dto.CategoriaRequest;
import br.com.contasdomesticas.api.dto.CategoriaResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.CategoriaMapper;
import br.com.contasdomesticas.api.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(categoriaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarRaizes() {
        return categoriaRepository.findByCategoriaPaiIsNull().stream().map(categoriaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id) {
        return categoriaMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        aplicar(categoria, request);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarEntidade(id);
        aplicar(categoria, request);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void remover(Long id) {
        categoriaRepository.delete(buscarEntidade(id));
    }

    private void aplicar(Categoria categoria, CategoriaRequest request) {
        categoria.setNome(request.nome());
        categoria.setCor(request.cor());
        categoria.setIcone(request.icone());
        categoria.setAtiva(request.ativa() == null || request.ativa());

        if (request.categoriaPaiId() != null) {
            Categoria pai = categoriaRepository.findById(request.categoriaPaiId())
                .orElseThrow(() -> new AplicacaoException(
                    "Categoria pai nao encontrada", HttpStatus.BAD_REQUEST));
            if (categoria.getId() != null && ehAncestralOuIgual(pai, categoria.getId())) {
                throw new AplicacaoException(
                    "Categoria nao pode ser filha dela mesma (ciclo)", HttpStatus.BAD_REQUEST);
            }
            categoria.setCategoriaPai(pai);
            categoria.setTipo(pai.getTipo()); // subcategoria herda o tipo da pai
        } else {
            if (request.tipo() == null) {
                throw new AplicacaoException(
                    "Tipo e obrigatorio para categoria raiz", HttpStatus.BAD_REQUEST);
            }
            categoria.setCategoriaPai(null);
            categoria.setTipo(request.tipo());
        }
    }

    // Verifica se 'id' aparece na cadeia de ancestrais de 'pai' (inclusive o proprio pai).
    private boolean ehAncestralOuIgual(Categoria pai, Long id) {
        Categoria atual = pai;
        while (atual != null) {
            if (id.equals(atual.getId())) {
                return true;
            }
            atual = atual.getCategoriaPai();
        }
        return false;
    }

    private Categoria buscarEntidade(Long id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Categoria nao encontrada com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
