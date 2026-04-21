package school.sptech.megusta.dto.categoria_fogazza;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class CategoriaFogazzaRequestDto {
    @NotBlank
    @Column(nullable = false)
    @Schema(example = "Salgada")
    private String sabor;

    public String getSabor() {
        return sabor;
    }

    public void setSabor(String sabor) {
        this.sabor = sabor;
    }
}
