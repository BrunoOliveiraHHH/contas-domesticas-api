package br.com.contasdomesticas.api.domain;

import jakarta.persistence.*;
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
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Login do usuario autenticado (ou "anonimo" quando nao houver). */
    @Column(name = "usuario", length = 100, nullable = false)
    private String usuario;

    @Column(name = "metodo_http", length = 10, nullable = false)
    private String metodoHttp;

    @Column(name = "endpoint", length = 500, nullable = false)
    private String endpoint;

    @Column(name = "status_resposta", nullable = false)
    private Integer statusResposta;

    @Column(name = "endereco_ip", length = 45, nullable = false)
    private String enderecoIp;

    @Column(name = "data_hora", nullable = false)
    private Instant dataHora;
}
