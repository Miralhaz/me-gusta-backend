package school.sptech.megusta.dto.consumo_geral_categoria;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumoGeralRequestDto {
    @NotNull
    @Positive
    @Schema(example = "7")
    private Integer intervalo;

}
