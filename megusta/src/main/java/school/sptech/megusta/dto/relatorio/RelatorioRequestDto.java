package school.sptech.megusta.dto.relatorio;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RelatorioRequestDto {

    private String tipo;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private List<TipoItemRelatorio> itens;

    private Integer insumoId;
}