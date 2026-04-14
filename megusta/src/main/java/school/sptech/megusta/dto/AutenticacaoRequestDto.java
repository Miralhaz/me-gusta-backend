package school.sptech.megusta.dto;

import jakarta.validation.constraints.NotBlank;

public class AutenticacaoRequestDto {

    @NotBlank
    private String login;

    @NotBlank
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
