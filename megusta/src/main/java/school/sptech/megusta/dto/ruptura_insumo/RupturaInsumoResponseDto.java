package school.sptech.megusta.dto.ruptura_insumo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class RupturaInsumoResponseDto {

    private Integer idInsumo;
    private String nomeInsumo;
    private String unidadeMedida;
    private BigDecimal quantidadeAtual;
    private BigDecimal estoqueMinimo;
    private BigDecimal consumoMedioDiario;
    private Integer diasDeCobertura;
    private String nivelRisco;
}