package school.sptech.megusta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.mapping.Join;

import java.time.LocalDateTime;

@Entity
@Table(name = "insumo")
@Getter
@Setter
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String codigoInsumo;

    @Positive
    @Column(nullable = false)
    private Double estoqueMinimo;

    @Positive
    @Column(nullable = false)
    private Double qtdAtual;

    @Column(nullable = false)
    private boolean ativo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @ManyToOne
    @JoinColumn(name = "fk_categoria_insumo", nullable = false)
    private CategoriaInsumo categoriaInsumo;

    @ManyToOne
    @JoinColumn(name = "fk_unidade_medida", nullable = false)
    private UnidadeMedida unidadeMedida;

    @ManyToOne
    @JoinColumn(name = "fk_status", nullable = false)
    private TipoStatus tipoStatus;

}
