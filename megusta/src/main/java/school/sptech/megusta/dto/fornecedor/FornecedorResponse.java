package school.sptech.megusta.dto.fornecedor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorResponse {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Laticínios São Paulo")
    private String nome;

    @Schema(example = "11.222.333/0001-44")
    private String cnpj;

    @Schema(example = "(11) 91234-5678")
    private String telefone;

    @Schema(example = "true")
    private Boolean ativo;

}
