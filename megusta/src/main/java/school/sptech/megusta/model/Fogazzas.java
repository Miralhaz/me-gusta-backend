package school.sptech.megusta.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Fogazzas")
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public CategoriaFogazza getCategoriaFogazza() {
        return categoriaFogazza;
    }

    public void setCategoriaFogazza(CategoriaFogazza categoriaFogazza) {
        this.categoriaFogazza = categoriaFogazza;
    }
}
