package school.sptech.megusta.dto.consumo_categoria;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumoCategoriaRequestDto {

    @NotBlank
    @Schema(example = "Laticínios")
    private String nomeCategoria;

    @Positive
    @Schema(example = "7")
    private Integer intervalo;
}
