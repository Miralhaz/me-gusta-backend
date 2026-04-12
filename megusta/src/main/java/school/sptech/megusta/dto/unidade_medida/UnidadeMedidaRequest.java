package school.sptech.megusta.dto.unidade_medida;

import jakarta.validation.constraints.NotBlank;

public class UnidadeMedidaRequest {

    @NotBlank
    private String unidade;

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
}
