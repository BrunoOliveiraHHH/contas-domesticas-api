package br.com.contasdomesticas.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Usuario do sistema. A senha e armazenada com hash BCrypt.
 */
@Entity
@Table(
        name = "usuario",
        uniqueConstraints = @UniqueConstraint(name = "uk_usuario_login", columnNames = "login")
)
@Getter
@Setter
@NoArgsConstructor
public class Usuario extends EntidadeAuditavel {

    @Column(name = "login", nullable = false, length = 100)
    private String login;

    @Column(name = "nome_exibicao", nullable = false, length = 150)
    private String nomeExibicao;

    @Column(name = "senha", nullable = false, length = 255)
    private String senha;
}
