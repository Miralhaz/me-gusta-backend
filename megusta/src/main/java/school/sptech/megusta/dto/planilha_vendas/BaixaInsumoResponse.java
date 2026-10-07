package school.sptech.megusta.dto.planilha_vendas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Resposta da importação de planilha de vendas: <b>um registro por insumo cuja
 * quantidade foi de fato alterada</b>, com o estoque antes da subtração, o
 * consumo da importação e o saldo restante.
 *
 * <p>Quando o mesmo insumo é consumido por mais de uma fogazza vendida, há um
 * único registro dele, com a soma das subtrações.
 */
@Getter
@Setter
@AllArgsConstructor
public class BaixaInsumoResponse {

    private String nomeInsumo;

    private String codigoInsumo;

    private String unidadeMedida;

    /** Quantidade do insumo antes da subtração. */
    private BigDecimal quantidadeAtual;

    /** Quantidade consumida pela importação. */
    private BigDecimal quantidadeSubtraida;

    /** Quantidade do insumo após a subtração. */
    private BigDecimal quantidadeAposSubtracao;
}
