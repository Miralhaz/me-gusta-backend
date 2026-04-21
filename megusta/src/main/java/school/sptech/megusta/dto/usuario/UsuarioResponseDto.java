package school.sptech.megusta.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;

public class UsuarioResponseDto {
    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Breno Costa")
    private String nome;

    @Schema(example = "breno@megusta.com")
    private String email;

    public UsuarioResponseDto() {
    }

    public UsuarioResponseDto(Integer id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
