package school.sptech.megusta.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "fogazza")
@Getter
@Setter
public class Fogazzas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private BigDecimal preco;

    @ManyToOne
    @JoinColumn(name = "fk_categoria_fogazza", nullable = false)
    private CategoriaFogazza categoriaFogazza;

}
