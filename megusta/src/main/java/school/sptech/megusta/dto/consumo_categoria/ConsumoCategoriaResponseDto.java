package school.sptech.megusta.dto.consumo_categoria;

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
public class ConsumoCategoriaResponseDto {

    private BigDecimal quantidade;
    private LocalDate dtConsumo;
}
