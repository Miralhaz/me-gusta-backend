package school.sptech.megusta.dto.consumo_categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumoCategoriaRequestDto {

    @NotBlank
    private String nomeCategoria;

    @Positive
    private Integer intervalo;
}
