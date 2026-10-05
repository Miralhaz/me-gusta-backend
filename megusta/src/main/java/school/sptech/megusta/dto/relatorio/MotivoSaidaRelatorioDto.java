package school.sptech.megusta.dto.relatorio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotivoSaidaRelatorioDto {

    private Integer motivoId;
    private String motivo;
    private BigDecimal quantidadeTotal;
}