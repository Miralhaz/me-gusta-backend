package school.sptech.megusta.dto.planilha_vendas;

import com.poiji.annotation.ExcelCellName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vendas {

    @ExcelCellName("Fogazza")
    private String nomeFogazza;

    @ExcelCellName("Quantidade")
    private Integer quantidadeFogazza;
}
