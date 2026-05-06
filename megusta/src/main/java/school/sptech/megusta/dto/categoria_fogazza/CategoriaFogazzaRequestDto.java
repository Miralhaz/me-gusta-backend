package school.sptech.megusta.dto.categoria_fogazza;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaFogazzaRequestDto {
    @NotBlank
    @Column(nullable = false)
    @Schema(example = "Salgada")
    private String nome;

}
