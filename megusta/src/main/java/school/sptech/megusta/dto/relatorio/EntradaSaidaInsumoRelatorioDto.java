package school.sptech.megusta.dto.relatorio;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EntradaSaidaInsumoRelatorioDto {

    private Integer insumoId;
    private String nomeInsumo;
    private BigDecimal totalEntradas;
    private BigDecimal totalSaidas;
    private BigDecimal diferenca;
    private String unidadeMedida;
}