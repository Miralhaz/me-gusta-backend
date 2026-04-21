package school.sptech.megusta.dto.categoria_fogazza;

import io.swagger.v3.oas.annotations.media.Schema;

public class CategoriaFogazzaResponseDto {

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
