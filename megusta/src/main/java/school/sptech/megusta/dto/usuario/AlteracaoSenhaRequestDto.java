package school.sptech.megusta.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlteracaoSenhaRequestDto {

    @NotBlank
    @Email
    @Schema(example = "breno@megusta.com")
    private String email;

    @NotBlank
    @Size(min = 8, max = 128, message = "A senha deve ter entre 8 e 128 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&\\-_+=#]).{8,128}$",
            message = "A senha deve conter letras maiúsculas, minúsculas, números e ao menos um caractere especial"
    )
    @Schema(example = "Senha@123")
    private String novaSenha;

}
