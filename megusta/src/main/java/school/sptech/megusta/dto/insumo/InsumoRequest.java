package school.sptech.megusta.dto.insumo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class InsumoRequest {

    @NotBlank
    @Schema(example = "Queijo Mussarela")
    private String nome;

    @NotBlank
    @Schema(example = "QM-001")
    private String codigoInsumo;

    @NotNull
    @Positive
    @Schema(example = "5.0")
    private Double estoqueMinimo;

    @NotNull
    @Positive
    @Schema(example = "20.0")
    private Double quantidadeAtual;

    @Schema(example = "true")
    private boolean ativo;

    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer fkCategoriaInsumo;

    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer fkUnidadeMedida;

    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer fkStatus;

    public Integer getFkStatus() {
        return fkStatus;
    }

    public void setFkStatus(Integer fkStatus) {
        this.fkStatus = fkStatus;
    }

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
