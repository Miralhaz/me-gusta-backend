package school.sptech.megusta.dto.unidade_medida;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnidadeMedidaResponse {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "kg")
    private String unidade;
}
