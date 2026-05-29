package school.sptech.megusta.dto.saida_estoque;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaidaEstoqueRequestDto {

    @Positive
    private Double quantidade;

    @Positive
    private Integer idInsumo;

    @Positive
    private Integer idUsuario;
}
