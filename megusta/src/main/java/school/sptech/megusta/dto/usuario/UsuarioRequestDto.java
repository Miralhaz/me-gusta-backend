package school.sptech.megusta.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsuarioRequestDto {

    @NotBlank
    @Column(nullable = false)
    @Schema(example = "Breno Costa")
    private String nome;

    @NotBlank
    @Column(nullable = false)
    @Schema(example = "senha123")
    private String senha;

    @NotBlank
    @Column(nullable = false)
    @Email
    @Schema(example = "breno@megusta.com")
    private String email;

    public UsuarioRequestDto() {
    }

    public UsuarioRequestDto(String nome, String senha, String email) {
        this.nome = nome;
        this.senha = senha;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
