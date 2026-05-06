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
public class FornecedorRequest {

    @NotBlank
    @Schema(example = "Laticínios São Paulo")
    private String nome;

    @NotBlank
    @CNPJ
    @Schema(example = "11.222.333/0001-44")
    private String cnpj;

    @NotBlank
    @Schema(example = "(11) 91234-5678")
    private String telefone;
}
