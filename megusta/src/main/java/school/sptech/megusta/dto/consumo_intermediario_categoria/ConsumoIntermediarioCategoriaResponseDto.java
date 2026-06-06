package school.sptech.megusta.dto.consumo_intermediario_categoria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsumoIntermediarioCategoriaResponseDto {

    private BigDecimal quantidade;
    private LocalDate dtConsumo;
    private String nomeCategoria;

}
