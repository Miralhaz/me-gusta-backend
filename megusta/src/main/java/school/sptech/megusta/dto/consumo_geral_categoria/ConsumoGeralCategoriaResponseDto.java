package school.sptech.megusta.dto.consumo_geral_categoria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.sptech.megusta.dto.consumo_intermediario_categoria.ConsumoIntermediarioCategoriaResponseDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsumoGeralCategoriaResponseDto {

    private String nomeCategoria;
    private List<ConsumoIntermediarioCategoriaResponseDto> consumos;

}
