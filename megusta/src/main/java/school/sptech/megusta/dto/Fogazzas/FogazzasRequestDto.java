package school.sptech.megusta.dto.Fogazzas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class FogazzasRequestDto {

    @NotBlank
    @Schema(example = "Fogazza de Queijo")
    private String nome;

    @NotNull
    @Positive
    @Schema(example = "15.00")
    private BigDecimal preco;

    @NotNull
    @Schema(example = "1")
    private Integer categoriaFogazzaId;

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

    public Integer getCategoriaFogazzaId() {
        return categoriaFogazzaId;
    }

    public void setCategoriaFogazzaId(Integer categoriaFogazzaId) {
        this.categoriaFogazzaId = categoriaFogazzaId;
    }
}
