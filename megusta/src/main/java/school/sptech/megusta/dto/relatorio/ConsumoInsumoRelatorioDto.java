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
public class ConsumoInsumoRelatorioDto {

    private Integer insumoId;
    private String nomeInsumo;
    private BigDecimal quantidadeConsumida;
    private String unidadeMedida;
}