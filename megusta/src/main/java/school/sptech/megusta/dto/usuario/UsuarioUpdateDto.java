package school.sptech.megusta.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateDto {

    @NotBlank
    @Schema(example = "Breno Costa")
    private String nome;

    @NotBlank
    @Email
    @Schema(example = "breno@megusta.com")
    private String email;

    @NotBlank
    @Size(max = 70, message = "O telefone deve ter no máximo 70 caracteres")
    @Schema(example = "(11) 91234-5678")
    private String telefone;

    // Senha se mantém fora, pois não deve ser incluída no mesmo endpoint
}
