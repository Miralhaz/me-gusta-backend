package school.sptech.megusta.dto.relatorio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatorioResumoResponseDto {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Relatório 01")
    private String nome;

    @Schema(example = "22/03/2026")
    private String data;

    @Schema(example = "08:00 - 17:00")
    private String horarioComercial;

    @Schema(example = "10 Mb")
    private String tamanho;
}
