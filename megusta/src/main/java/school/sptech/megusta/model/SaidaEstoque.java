package school.sptech.megusta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "saida_estoque")
@Getter
@Setter
public class SaidaEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "fk_insumo", nullable = false)
    private Insumo insumo;

    @ManyToOne
    @JoinColumn(name = "fk_usuario", nullable = false)
    private Usuario usuario;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    @CreationTimestamp
    @Column(name = "dt_saida", nullable = false, updatable = false)
    private LocalDateTime dtSaida;

    @ManyToOne
    @JoinColumn(name = "fk_motivo", nullable = false)
    private Motivo motivo;
}