package school.sptech.megusta.dto.insumo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class InsumoRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String codigoInsumo;

    @NotNull
    @Positive
    private Double estoqueMinimo;

    @NotNull
    @Positive
    private Double quantidadeAtual;

    private boolean ativo;

    @NotNull
    @Positive
    private Integer fkCategoriaInsumo;

    @NotNull
    @Positive
    private Integer fkUnidadeMedida;

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getCodigoInsumo() {
        return codigoInsumo;
    }

    public void setCodigoInsumo(String codigoInsumo) {
        this.codigoInsumo = codigoInsumo;
    }

    public Double getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Double estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Integer getFkCategoriaInsumo() {
        return fkCategoriaInsumo;
    }

    public void setFkCategoriaInsumo(Integer fkCategoriaInsumo) {
        this.fkCategoriaInsumo = fkCategoriaInsumo;
    }

    public Integer getFkUnidadeMedida() {
        return fkUnidadeMedida;
    }

    public void setFkUnidadeMedida(Integer fkUnidadeMedida) {
        this.fkUnidadeMedida = fkUnidadeMedida;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(Double quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }
}
