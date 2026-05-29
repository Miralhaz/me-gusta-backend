package school.sptech.megusta.dto.saida_estoque;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SaidaEstoqueResponseDto {

    private Integer id;
    private Double quantidade;
    private LocalDateTime dtSaida;
    private Integer idInsumo;

    @Getter
    @Setter
    public class Usuario{
        private Integer idUsuario;
        private String nome;
        private String email;
    }
}
