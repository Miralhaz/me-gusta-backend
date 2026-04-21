package school.sptech.megusta.dto.tipo_status;

import io.swagger.v3.oas.annotations.media.Schema;

public class TipoStatusResponse {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Em preparo")
    private String nome;

    public TipoStatusResponse() {
    }

    public TipoStatusResponse(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
