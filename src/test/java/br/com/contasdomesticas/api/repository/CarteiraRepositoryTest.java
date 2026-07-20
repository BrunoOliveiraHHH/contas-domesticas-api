package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.config.JpaAuditingConfig;
import br.com.contasdomesticas.api.domain.Carteira;
import br.com.contasdomesticas.api.domain.TipoCarteira;
import br.com.contasdomesticas.api.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class CarteiraRepositoryTest {

    @Autowired
    private CarteiraRepository carteiraRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void devePersistirCarteiraFamiliarComAuditoria() {
        Carteira carteira = new Carteira();
        carteira.setNome("Comum");
        carteira.setTipo(TipoCarteira.FAMILIAR);
        carteira.setSaldoInicial(BigDecimal.ZERO);

        Carteira salva = carteiraRepository.save(carteira);

        assertThat(salva.getId()).isNotNull();
        assertThat(salva.getCriadoEm()).isNotNull();
        assertThat(salva.getMoeda()).isEqualTo("BRL");
        assertThat(salva.isAtiva()).isTrue();
    }

    @Test
    void deveEncontrarPorDono() {
        Usuario dono = new Usuario();
        dono.setLogin("bruno");
        dono.setNomeExibicao("Bruno");
        dono.setSenha("hash-fake");
        dono = usuarioRepository.save(dono);

        Carteira carteira = new Carteira();
        carteira.setNome("Carteira do Bruno");
        carteira.setTipo(TipoCarteira.INDIVIDUAL);
        carteira.setDono(dono);
        carteiraRepository.save(carteira);

        assertThat(carteiraRepository.findByDonoId(dono.getId())).hasSize(1);
    }
}
