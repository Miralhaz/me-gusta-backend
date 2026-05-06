package school.sptech.megusta.dto.categoria_insumo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaInsumoResponseDto {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Laticínios")
    private String nome;
}
