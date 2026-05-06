package school.sptech.megusta.dto.insumo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InsumoResponse {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Queijo Mussarela")
    private String nome;

    @Schema(example = "QM-001")
    private String codigoInsumo;

    @Schema(example = "5.0")
    private Double estoqueMinimo;

    @Schema(example = "20.0")
    private Double quantidadeAtual;

    @Schema(example = "true")
    private boolean ativo;

    private InsumoCategoria insumoCategoria;
    private UnidadeInsumo unidadeInsumo;
    private TipoStatusInsumo tipoStatus;

    @Getter
    @Setter
    public static class TipoStatusInsumo {
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "OK")
        private String nome;

    }

    @Getter
    @Setter
    public static class InsumoCategoria {
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "Laticínios")
        private String nome;
    }

    @Getter
    @Setter
    public static class UnidadeInsumo {
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "kg")
        private String unidade;
    }
}
