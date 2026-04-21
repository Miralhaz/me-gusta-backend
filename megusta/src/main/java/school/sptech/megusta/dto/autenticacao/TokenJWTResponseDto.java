package school.sptech.megusta.dto.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;

public class TokenJWTResponseDto {
    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJicmVub0BtZWd1c3RhLmNvbSJ9.abc123")
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
