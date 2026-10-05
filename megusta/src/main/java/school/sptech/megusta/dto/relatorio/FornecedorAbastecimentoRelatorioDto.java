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
public class FornecedorAbastecimentoRelatorioDto {

    private Integer fornecedorId;
    private String nomeFornecedor;
    private BigDecimal quantidadeTotal;
    private BigDecimal valorTotal;
}