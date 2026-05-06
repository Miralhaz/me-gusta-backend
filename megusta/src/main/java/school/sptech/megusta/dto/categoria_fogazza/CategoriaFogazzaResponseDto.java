package school.sptech.megusta.dto.categoria_fogazza;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaFogazzaResponseDto {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Salgada")
    private String nome;
}
