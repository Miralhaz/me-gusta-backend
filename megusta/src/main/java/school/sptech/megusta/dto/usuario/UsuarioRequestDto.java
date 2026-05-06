package school.sptech.megusta.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDto {

    @NotBlank
    @Column(nullable = false)
    @Schema(example = "Breno Costa")
    private String nome;

    @NotBlank
    @Column(nullable = false)
    @Schema(example = "senha123")
    private String senha;

    @NotBlank
    @Column(nullable = false)
    @Email
    @Schema(example = "breno@megusta.com")
    private String email;

}
