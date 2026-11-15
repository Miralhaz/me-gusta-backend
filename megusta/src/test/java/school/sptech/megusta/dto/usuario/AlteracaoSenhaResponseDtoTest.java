package school.sptech.megusta.dto.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testes de AlteracaoSenhaResponseDto")
class AlteracaoSenhaResponseDtoTest {

    @Test
    @DisplayName("Deve serializar a mensagem no campo mensagem")
    void deveSerializarMensagem() throws Exception {
        AlteracaoSenhaResponseDto dto = new AlteracaoSenhaResponseDto("Senha alterada com sucesso");

        String json = new ObjectMapper().writeValueAsString(dto);

        assertEquals("{\"mensagem\":\"Senha alterada com sucesso\"}", json);
    }
}
