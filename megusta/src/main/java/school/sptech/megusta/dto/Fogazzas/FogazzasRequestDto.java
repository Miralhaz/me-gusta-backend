package school.sptech.megusta.dto.Fogazzas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FogazzasRequestDto {

    @NotBlank
    @Schema(example = "Fogazza de Queijo")
    private String nome;

    @NotNull
    @Positive
    @Schema(example = "15.00")
    private BigDecimal preco;

    @NotNull
    @Schema(example = "1")
    private Integer categoriaFogazzaId;
}
