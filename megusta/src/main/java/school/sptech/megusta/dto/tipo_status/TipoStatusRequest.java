package school.sptech.megusta.dto.tipo_status;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class TipoStatusRequest {

    @NotBlank
    @Column(nullable = false)
    @Schema(example = "Em preparo")
    private String nome;

    public TipoStatusRequest() {
    }

    public TipoStatusRequest(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
