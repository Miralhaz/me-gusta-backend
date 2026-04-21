package school.sptech.megusta.dto.Fogazzas;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class FogazzasResponseDto {
    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Fogazza de Queijo")
    private String nome;

    @Schema(example = "15.00")
    private BigDecimal preco;


    private CategoriaFogazzaDto categoriaFogazza;

    public static class CategoriaFogazzaDto{
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "Salgada")
        private String sabor;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSabor() {
            return sabor;
        }

        public void setSabor(String sabor) {
            this.sabor = sabor;
        }
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

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public CategoriaFogazzaDto getCategoriaFogazza() {
        return categoriaFogazza;
    }

    public void setCategoriaFogazza(CategoriaFogazzaDto categoriaFogazza) {
        this.categoriaFogazza = categoriaFogazza;
    }
}
