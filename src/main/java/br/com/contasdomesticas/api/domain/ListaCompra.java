package br.com.contasdomesticas.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Lista de compras (mantimentos ou construcao). O estabelecimento e escolhido
 * por item. Listas nao fechadas ficam no historico (reutilizaveis).
 */
@Entity
@Table(name = "lista_compra")
@Getter
@Setter
@NoArgsConstructor
public class ListaCompra extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 15)
    private TipoLista tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carteira_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_lista_compra_carteira"))
    private Carteira carteira;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private StatusLista status = StatusLista.ABERTA;
}
