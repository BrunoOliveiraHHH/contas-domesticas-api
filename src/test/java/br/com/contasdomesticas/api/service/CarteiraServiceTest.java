package br.com.contasdomesticas.api.service;

import br.com.contasdomesticas.api.domain.Carteira;
import br.com.contasdomesticas.api.domain.TipoCarteira;
import br.com.contasdomesticas.api.dto.CarteiraRequest;
import br.com.contasdomesticas.api.dto.CarteiraResponse;
import br.com.contasdomesticas.api.exception.AplicacaoException;
import br.com.contasdomesticas.api.mapper.CarteiraMapper;
import br.com.contasdomesticas.api.repository.CarteiraRepository;
import br.com.contasdomesticas.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarteiraServiceTest {

    @Mock
    private CarteiraRepository carteiraRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CarteiraMapper carteiraMapper;

    @InjectMocks
    private CarteiraService carteiraService;

    private CarteiraResponse dummy() {
        return new CarteiraResponse(1L, "Comum", TipoCarteira.FAMILIAR, null, null,
            BigDecimal.ZERO, "BRL", null, null, true, null, null);
    }

    @Test
    void deveCriarCarteiraFamiliarSemDono() {
        CarteiraRequest request = new CarteiraRequest(
            "Comum", TipoCarteira.FAMILIAR, null, BigDecimal.ZERO, "BRL", null, null, true);
        when(carteiraRepository.save(any(Carteira.class))).thenAnswer(inv -> inv.getArgument(0));
        when(carteiraMapper.toResponse(any(Carteira.class))).thenReturn(dummy());

        CarteiraResponse response = carteiraService.criar(request);

        assertThat(response.tipo()).isEqualTo(TipoCarteira.FAMILIAR);
        ArgumentCaptor<Carteira> captor = ArgumentCaptor.forClass(Carteira.class);
        verify(carteiraRepository).save(captor.capture());
        assertThat(captor.getValue().getDono()).isNull();
        assertThat(captor.getValue().getMoeda()).isEqualTo("BRL");
    }

    @Test
    void naoDeveCriarIndividualSemDono() {
        CarteiraRequest request = new CarteiraRequest(
            "Privada", TipoCarteira.INDIVIDUAL, null, null, null, null, null, null);

        assertThatThrownBy(() -> carteiraService.criar(request))
            .isInstanceOf(AplicacaoException.class);
        verify(carteiraRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarIndividualComDonoInexistente() {
        CarteiraRequest request = new CarteiraRequest(
            "Privada", TipoCarteira.INDIVIDUAL, 99L, null, null, null, null, null);
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carteiraService.criar(request))
            .isInstanceOf(AplicacaoException.class);
        verify(carteiraRepository, never()).save(any());
    }

    @Test
    void deveLancarQuandoCarteiraNaoEncontrada() {
        when(carteiraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carteiraService.buscarPorId(99L))
            .isInstanceOf(AplicacaoException.class);
    }
}
