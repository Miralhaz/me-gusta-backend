package school.sptech.megusta.dto.categoria_insumo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class CategoriaInsumoRequestDto {

    @NotBlank
    @Column(nullable = false)
    @Schema(example = "Laticínios")
    private String nome;

    public CategoriaInsumoRequestDto(String nome) {
        this.nome = nome;
    }

    public CategoriaInsumoRequestDto() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
