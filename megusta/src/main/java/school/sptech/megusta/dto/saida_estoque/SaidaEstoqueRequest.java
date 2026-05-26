package school.sptech.megusta.dto.saida_estoque;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class SaidaEstoqueRequest implements Serializable {

    @NotNull
    @Positive
    private Integer fkInsumo;

    @NotNull
    @Positive
    private Integer fkUsuario;

    @NotNull
    @Positive
    private BigDecimal quantidade;

    private LocalDateTime dtSaida;

}