package school.sptech.megusta.dto.entrada_estoque;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class EntradaEstoqueResponse implements Serializable {

    @Schema(example = "1")
    private Integer id;

    private InsumoEntrada insumo;

    private UsuarioEntrada usuario;

    private FornecedorEntrada fornecedor;

    private TipoStatusEntrada tipoStatus;

    private UnidadeMedidaEntrada unidadeMedida;

    @Schema(example = "1.50")
    private BigDecimal quantidadeAbsoluta;

    @Schema(example = "1.50")
    private BigDecimal quantidadeRelativa;

    @Schema(example = "2025-05-25T14:30:00")
    private LocalDateTime dtEntrada;

    @Schema(example = "LOTE-001")
    private String lote;

    @Schema(example = "2025-06-15")
    private LocalDate dtValidade;

    @Schema(example = "2025-05-01")
    private LocalDate dtPedido;

    @Schema(example = "150.0000")
    private BigDecimal vlTotal;


    @Getter
    @Setter
    public static class InsumoEntrada {
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "Trigo")
        private String nome;

        @Schema(example = "TR-001")
        private String codigoInsumo;
    }

    @Getter
    @Setter
    public static class UsuarioEntrada {
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "João Silva")
        private String nome;

        @Schema(example = "joao@email.com")
        private String email;
    }

    @Getter
    @Setter
    public static class FornecedorEntrada {
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "Fornecedor XYZ")
        private String nome;

        @Schema(example = "12345678000190")
        private String cnpj;
    }

    @Getter
    @Setter
    public static class TipoStatusEntrada {
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "Recebido")
        private String nome;
    }

    @Getter
    @Setter
    public static class UnidadeMedidaEntrada {
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "kg")
        private String unidade;
    }

}