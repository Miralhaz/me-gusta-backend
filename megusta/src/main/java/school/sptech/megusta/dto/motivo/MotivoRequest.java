package school.sptech.megusta.dto.motivo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MotivoRequest {

    @NotBlank
    @Length(max = 70)
    @Schema(example = "Vendido")
    private String nome;
}
