package br.com.contasdomesticas.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Registro de auditoria de acesso: cada requisicao HTTP tratada pela API
 * gera uma linha (quem, metodo, endpoint, status, ip, quando).
 */
@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
public class Auditoria extends BaseEntity {

    /** Login do usuario autenticado (ou "anonimo" quando nao houver). */
    @Column(name = "usuario", length = 100)
    private String usuario;

    @Column(name = "metodo_http", length = 10)
    private String metodoHttp;

    @Column(name = "endpoint", length = 500)
    private String endpoint;

    @Column(name = "status_resposta")
    private Integer statusResposta;

    @Column(name = "endereco_ip", length = 45)
    private String enderecoIp;

    @Column(name = "data_hora")
    private Instant dataHora;
}
