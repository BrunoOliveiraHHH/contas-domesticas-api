package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.ListaCompra;
import br.com.contasdomesticas.api.domain.StatusLista;
import br.com.contasdomesticas.api.dto.ListaCompraRequest;
import br.com.contasdomesticas.api.dto.ListaCompraResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.ListaCompraMapper;
import br.com.contasdomesticas.api.repository.CarteiraRepository;
import br.com.contasdomesticas.api.repository.ListaCompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListaCompraService {

    private final ListaCompraRepository listaCompraRepository;
    private final CarteiraRepository carteiraRepository;
    private final ListaCompraMapper listaCompraMapper;

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

    private void aplicar(ListaCompra lista, ListaCompraRequest request) {
        lista.setNome(request.nome());
        lista.setTipo(request.tipo());
        lista.setData(request.data() != null ? request.data() : LocalDate.now());
        lista.setCarteira(carteiraRepository.findById(request.carteiraId())
            .orElseThrow(() -> new AplicacaoException("Carteira informada nao existe", HttpStatus.BAD_REQUEST)));
    }

    ListaCompra buscarEntidade(Long id) {
        return listaCompraRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Lista de compra nao encontrada com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
