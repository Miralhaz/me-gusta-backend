package school.sptech.megusta.dto.autenticacao;

public class TokenJWTResponseDto {
    private String token;

    public TokenJWTResponseDto(String token) {
        this.token = token;
    }

    public TokenJWTResponseDto() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
