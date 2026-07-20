package br.com.contasdomesticas.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mercado / fornecedor onde a compra e feita; base do historico de preco.
 */
@Entity
@Table(name = "mercado")
@Getter
@Setter
@NoArgsConstructor
public class Mercado extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMercado tipo;

    @Column(name = "endereco", length = 200)
    private String endereco;

    @Column(name = "bairro", length = 100)
    private String bairro;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
}
