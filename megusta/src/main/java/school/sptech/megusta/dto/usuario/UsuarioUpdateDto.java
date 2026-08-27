package school.sptech.megusta.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    // Senha se mantém fora, pois não deve ser incluída no mesmo endpoint
}
