package school.sptech.megusta.dto.categoria_insumo;

import io.swagger.v3.oas.annotations.media.Schema;

public class CategoriaInsumoResponseDto {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Laticínios")
    private String nome;

    public CategoriaInsumoResponseDto(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public CategoriaInsumoResponseDto() {
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
}
