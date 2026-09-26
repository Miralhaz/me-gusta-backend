package school.sptech.megusta.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    // Telefone aceito apenas como dígitos, com 10 ou 11 caracteres.
    // A formatação visual é responsabilidade do frontend.
    @NotBlank
    @Pattern(regexp = "^[0-9]{10,11}$", message = "O telefone deve conter apenas dígitos, com 10 ou 11 caracteres")
    @Schema(example = "11999999999")
    private String telefone;

    // Senha se mantém fora, pois não deve ser incluída no mesmo endpoint
}
