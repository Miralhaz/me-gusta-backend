package school.sptech.megusta.dto.relatorio;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MediaConsumoRelatorioDto {

    private Integer insumoId;
    private String nomeInsumo;
    private BigDecimal mediaDiaria;
    private String unidadeMedida;
}