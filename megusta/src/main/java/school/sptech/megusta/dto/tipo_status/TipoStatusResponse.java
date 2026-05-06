package school.sptech.megusta.dto.tipo_status;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoStatusResponse {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Em preparo")
    private String nome;

}
