package school.sptech.megusta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrada_estoque")
@Getter
@Setter
public class EntradaEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "fk_insumo", nullable = false)
    private Insumo insumo;

    @ManyToOne
    @JoinColumn(name = "fk_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "fk_fornecedor", nullable = false)
    private Fornecedor fornecedor;

    @ManyToOne
    @JoinColumn(name = "fk_tipo_status", nullable = false)
    private TipoStatus tipoStatus;

    @ManyToOne
    @JoinColumn(name = "fk_unidade_medida", nullable = false)
    private UnidadeMedida unidadeMedida;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidadeAbsoluta;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidadeRelativa;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dtEntrada;

    @Column(length = 50)
    private String lote;

    @Column
    private LocalDate dtValidade;

    @Column
    private LocalDate dtPedido;

    @Column(precision = 19, scale = 4)
    private BigDecimal vlTotal;

}