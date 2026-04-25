package school.sptech.megusta.dto.categoria_fogazza;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class CategoriaFogazzaRequestDto {
    @NotBlank
    @Column(nullable = false)
    @Schema(example = "Salgada")
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
