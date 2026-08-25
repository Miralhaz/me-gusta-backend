package school.sptech.megusta.dto.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AutenticacaoRequestDto {

    // 2° vulnerabilidade (A07 - Identification and Authentication Failures):
    // O login aceitava qualquer string, sem validar formato de e-mail nem tamanho
    // máximo, facilitando enumeração de usuários e payloads abusivos.
    // Correção: exigir formato de e-mail e limitar tamanho máximo razoável.
    @NotBlank
    @Email
    @Size(max = 100)
    @Schema(example = "breno@megusta.com")
    private String login;

    @NotBlank
    @Size(max = 128)
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
