package school.sptech.megusta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.mapping.Join;

import java.time.LocalDateTime;

@Entity
@Table(name = "insumo")
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

    public Double getQtdAtual() {
        return qtdAtual;
    }

    public void setQtdAtual(Double qtdAtual) {
        this.qtdAtual = qtdAtual;
    }

    public LocalDateTime getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(LocalDateTime dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public TipoStatus getTipoStatus() {
        return tipoStatus;
    }

    public void setTipoStatus(TipoStatus tipoStatus) {
        this.tipoStatus = tipoStatus;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public CategoriaInsumo getCategoriaInsumo() {
        return categoriaInsumo;
    }

    public void setCategoriaInsumo(CategoriaInsumo categoriaInsumo) {
        this.categoriaInsumo = categoriaInsumo;
    }

    public String getCodigoInsumo() {
        return codigoInsumo;
    }

    public void setCodigoInsumo(String codigoInsumo) {
        this.codigoInsumo = codigoInsumo;
    }

    public LocalDateTime getDataCadastro() {
        return dtCadastro;
    }

    public void setDataCadastro(LocalDateTime dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public Double getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Double estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

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

    public Double getQuantidadeAtual() {
        return qtdAtual;
    }

    public void setQuantidadeAtual(Double qtdAtual) {
        this.qtdAtual = qtdAtual;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }
}
