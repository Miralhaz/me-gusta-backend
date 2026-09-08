package school.sptech.megusta.dto.movimentacao;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class MovimentacaoResponse implements Serializable {

    private Integer id;
    private String tipo;
    private InsumoMovimentacao insumo;
    private UsuarioMovimentacao usuario;
    private BigDecimal quantidade;
    private LocalDateTime data;
    private String detalhe;

    @Getter
    @Setter
    public static class InsumoMovimentacao {
        private Integer id;
        private String nome;
        private String unidade;
    }

    @Getter
    @Setter
    public static class UsuarioMovimentacao {
        private Integer id;
        private String nome;
    }

}