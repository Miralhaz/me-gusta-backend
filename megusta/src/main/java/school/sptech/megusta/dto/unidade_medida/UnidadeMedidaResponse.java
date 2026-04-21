package school.sptech.megusta.dto.unidade_medida;

import io.swagger.v3.oas.annotations.media.Schema;

public class UnidadeMedidaResponse {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "kg")
    private String unidade;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
}
