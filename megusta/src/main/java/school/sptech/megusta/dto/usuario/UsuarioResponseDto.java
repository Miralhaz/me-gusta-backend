package school.sptech.megusta.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDto {
    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Breno Costa")
    private String nome;

    @Schema(example = "breno@megusta.com")
    private String email;


}
