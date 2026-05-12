package school.sptech.megusta.dto.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class AutenticacaoRequestDto {

    @NotBlank
    @Schema(example = "breno@megusta.com")
    private String login;

    @NotBlank
    @Schema(example = "senha123")
    private String senha;

    public AutenticacaoRequestDto() {
    }

    public AutenticacaoRequestDto(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
