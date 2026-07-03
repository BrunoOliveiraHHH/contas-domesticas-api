package br.com.contasdomesticas.api.repository;

import br.com.contasdomesticas.api.domain.Auditoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuditoriaRepositoryTest {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Test
    void devePersistirRegistroDeAuditoria() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario("bruno");
        auditoria.setMetodoHttp("POST");
        auditoria.setEndpoint("/api/v1/usuarios");
        auditoria.setStatusResposta(201);
        auditoria.setEnderecoIp("127.0.0.1");
        auditoria.setDataHora(Instant.now());

        Auditoria salvo = auditoriaRepository.save(auditoria);

        assertThat(salvo.getId()).isNotNull();
        assertThat(auditoriaRepository.findAll()).hasSize(1);
    }
}
