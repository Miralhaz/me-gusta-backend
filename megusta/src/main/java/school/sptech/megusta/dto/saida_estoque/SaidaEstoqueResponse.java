package school.sptech.megusta.dto.saida_estoque;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class SaidaEstoqueResponse implements Serializable {

    private Integer id;

    private InsumoSaida insumo;

    private UsuarioSaida usuario;

    private BigDecimal quantidade;

    private LocalDateTime dtSaida;

    @Getter
    @Setter
    public static class InsumoSaida {
        private Integer id;
        private String nome;
    }

    @Getter
    @Setter
    public static class UsuarioSaida {
        private Integer id;
        private String nome;
    }
}