package school.sptech.megusta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.repository.UsuarioRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Testes de integração de alteração de senha")
class UsuarioPasswordResetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario criarUsuario(String email, String senha) {
        return repository.save(new Usuario(
                null, "Usuário Teste", email, passwordEncoder.encode(senha), "(11) 90000-0000"));
    }

    @Test
    @DisplayName("Deve permitir reset sem JWT e retornar 200 com mensagem, persistindo o hash")
    void devePermitirResetSemJwt() throws Exception {
        Usuario usuario = criarUsuario("reset.integration@teste.com", "SenhaAntiga@1");

        String corpo = objectMapper.writeValueAsString(Map.of(
                "email", "reset.integration@teste.com",
                "novaSenha", "NovaSenha@123"
        ));

        mockMvc.perform(put("/usuarios/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").isNotEmpty());

        Usuario atualizado = repository.findFirstByEmail("reset.integration@teste.com").orElseThrow();

        assertNotEquals("NovaSenha@123", atualizado.getSenha());
        assertTrue(passwordEncoder.matches("NovaSenha@123", atualizado.getSenha()));
        assertFalse(passwordEncoder.matches("SenhaAntiga@1", atualizado.getSenha()));
        assertEquals(usuario.getId(), atualizado.getId());
    }

    @Test
    @DisplayName("Deve retornar 400 para senha fraca")
    void deveRetornar400ParaSenhaFraca() throws Exception {
        criarUsuario("reset.fraca@teste.com", "SenhaAntiga@1");

        String corpo = objectMapper.writeValueAsString(Map.of(
                "email", "reset.fraca@teste.com",
                "novaSenha", "fraca"
        ));

        mockMvc.perform(put("/usuarios/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest());

        Usuario inalterado = repository.findFirstByEmail("reset.fraca@teste.com").orElseThrow();
        assertTrue(passwordEncoder.matches("SenhaAntiga@1", inalterado.getSenha()));
    }

    @Test
    @DisplayName("Deve retornar 400 para e-mail inválido")
    void deveRetornar400ParaEmailInvalido() throws Exception {
        String corpo = objectMapper.writeValueAsString(Map.of(
                "email", "nao-eh-email",
                "novaSenha", "NovaSenha@123"
        ));

        mockMvc.perform(put("/usuarios/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 404 para e-mail não cadastrado")
    void deveRetornar404ParaEmailNaoCadastrado() throws Exception {
        String corpo = objectMapper.writeValueAsString(Map.of(
                "email", "nao.existe@teste.com",
                "novaSenha", "NovaSenha@123"
        ));

        mockMvc.perform(put("/usuarios/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve manter as demais rotas de usuários protegidas")
    void deveManterDemaisRotasProtegidas() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().is4xxClientError());
    }
}
