package school.sptech.megusta.dto.unidade_medida;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnidadeMedidaRequest {

    @NotBlank
    @Schema(example = "kg")
    private String unidade;
}
