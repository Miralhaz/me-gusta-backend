package school.sptech.megusta.dto.unidade_medida;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class UnidadeMedidaRequest {

    @NotBlank
    @Schema(example = "kg")
    private String unidade;

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
}
