package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Carteira;
import br.com.contasdomesticas.api.domain.TipoCarteira;
import br.com.contasdomesticas.api.domain.Usuario;
import br.com.contasdomesticas.api.dto.CarteiraRequest;
import br.com.contasdomesticas.api.dto.CarteiraResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.CarteiraMapper;
import br.com.contasdomesticas.api.repository.CarteiraRepository;
import br.com.contasdomesticas.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarteiraService {

    private final CarteiraRepository carteiraRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarteiraMapper carteiraMapper;

    @Transactional(readOnly = true)
    public List<CarteiraResponse> listar() {
        return carteiraRepository.findAll().stream()
            .map(carteiraMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public CarteiraResponse buscarPorId(Long id) {
        return carteiraMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public CarteiraResponse criar(CarteiraRequest request) {
        Carteira carteira = new Carteira();
        aplicar(carteira, request);
        return carteiraMapper.toResponse(carteiraRepository.save(carteira));
    }

    @Transactional
    public CarteiraResponse atualizar(Long id, CarteiraRequest request) {
        Carteira carteira = buscarEntidade(id);
        aplicar(carteira, request);
        return carteiraMapper.toResponse(carteiraRepository.save(carteira));
    }

    @Transactional
    public void remover(Long id) {
        carteiraRepository.delete(buscarEntidade(id));
    }

    private void aplicar(Carteira carteira, CarteiraRequest request) {
        carteira.setNome(request.nome());
        carteira.setTipo(request.tipo());
        carteira.setSaldoInicial(request.saldoInicial() != null ? request.saldoInicial() : BigDecimal.ZERO);
        carteira.setMoeda(request.moeda() != null && !request.moeda().isBlank() ? request.moeda() : "BRL");
        carteira.setCor(request.cor());
        carteira.setIcone(request.icone());
        carteira.setAtiva(request.ativa() == null || request.ativa());
        aplicarDono(carteira, request);
    }

    private void aplicarDono(Carteira carteira, CarteiraRequest request) {
        if (request.tipo() == TipoCarteira.INDIVIDUAL) {
            if (request.donoId() == null) {
                throw new AplicacaoException("Carteira individual exige um dono", HttpStatus.BAD_REQUEST);
            }
            Usuario dono = usuarioRepository.findById(request.donoId())
                .orElseThrow(() -> new AplicacaoException("Dono informado nao existe", HttpStatus.BAD_REQUEST));
            carteira.setDono(dono);
        } else {
            // Carteira familiar e compartilhada: nao tem dono.
            carteira.setDono(null);
        }
    }

    private Carteira buscarEntidade(Long id) {
        return carteiraRepository.findById(id)
            .orElseThrow(() -> new AplicacaoException(
                "Carteira nao encontrada com o id: " + id, HttpStatus.NOT_FOUND));
    }
}
