package school.sptech.megusta.dto.insumo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsumoRequest {

    @NotBlank
    @Schema(example = "Queijo Mussarela")
    private String nome;

    @NotBlank
    @Schema(example = "QM-001")
    private String codigoInsumo;

    @NotNull
    @Positive
    @Schema(example = "5.0")
    private Double estoqueMinimo;

    @NotNull
    @Positive
    @Schema(example = "20.0")
    private Double quantidadeAtual;

    @Schema(example = "true")
    private boolean ativo;

    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer fkCategoriaInsumo;

    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer fkUnidadeMedida;

    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer fkStatus;

}
