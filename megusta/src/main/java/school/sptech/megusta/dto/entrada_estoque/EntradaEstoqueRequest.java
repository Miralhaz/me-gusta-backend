package school.sptech.megusta.dto.entrada_estoque;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EntradaEstoqueRequest implements Serializable {

    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer fkInsumo;

    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer fkUsuario;

    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer fkFornecedor;

    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer fkTipoStatus;

    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer fkUnidadeMedida;

    @NotNull
    @Positive
    @Schema(example = "1.50")
    private BigDecimal quantidadeAbsoluta;

    @NotNull
    @Positive
    @Schema(example = "1.50")
    private BigDecimal quantidadeRelativa;

    @Schema(example = "LOTE-001")
    private String lote;

    @Schema(example = "2025-06-15")
    private LocalDate dtValidade;

    @Schema(example = "2025-05-01")
    private LocalDate dtPedido;

    @Schema(example = "150.0000")
    private BigDecimal vlTotal;

}