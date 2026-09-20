package school.sptech.megusta.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Mapeamento JPA da tabela de junção {@code fogazza_insumo} existente no banco
 * (definida em {@code megustaV06.sql}). Apenas mapeamento — nenhum DDL/alteração
 * no banco de dados.
 */
@Entity
@Table(name = "fogazza_insumo")
@Getter
@Setter
@IdClass(FogazzaInsumoId.class)
public class FogazzaInsumo {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_fogazza", nullable = false)
    private Fogazzas fogazza;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_insumo", nullable = false)
    private Insumo insumo;

    @Column(name = "quantidade_insumo", precision = 10, scale = 2)
    private BigDecimal quantidadeInsumo;
}