package school.sptech.megusta.dto.Fogazzas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FogazzasResponseDto {
    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Fogazza de Queijo")
    private String nome;

    @Schema(example = "15.00")
    private BigDecimal preco;


    private CategoriaFogazzaDto categoriaFogazza;

    @Getter
    @Setter
    public static class CategoriaFogazzaDto{
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "Salgada")
        private String nome;
    }
}
