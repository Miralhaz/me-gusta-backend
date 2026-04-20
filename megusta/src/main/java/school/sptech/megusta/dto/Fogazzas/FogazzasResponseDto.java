package school.sptech.megusta.dto.Fogazzas;

import java.math.BigDecimal;

public class FogazzasResponseDto {
    private Integer id;
    private String nome;
    private BigDecimal preco;
    private CategoriaFogazzaDto categoriaFogazza;

    public static class CategoriaFogazzaDto{
        private Integer id;
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
