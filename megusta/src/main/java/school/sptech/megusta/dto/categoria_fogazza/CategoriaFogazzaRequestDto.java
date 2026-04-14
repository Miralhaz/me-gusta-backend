package school.sptech.megusta.dto.categoria_fogazza;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class CategoriaFogazzaRequestDto {
    @NotBlank
    @Column(nullable = false)
    private String sabor;

    public String getSabor() {
        return sabor;
    }

    public void setSabor(String sabor) {
        this.sabor = sabor;
    }
}
