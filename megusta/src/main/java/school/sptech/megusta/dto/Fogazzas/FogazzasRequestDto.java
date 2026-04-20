package school.sptech.megusta.dto.Fogazzas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class FogazzasRequestDto {

    @NotBlank
    private String nome;

    @NotNull
    @Positive
    private BigDecimal preco;

    @NotNull
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
