package school.sptech.megusta.dto.tipo_status;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoStatusRequest {

    @NotBlank
    @Column(nullable = false)
    @Schema(example = "Em preparo")
    private String nome;

}
