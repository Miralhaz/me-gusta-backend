package school.sptech.megusta.dto.motivo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MotivoResponse {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Vendido")
    private String nome;
}
