package school.sptech.megusta.dto.autenticacao;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de validação do AutenticacaoRequestDto (A07)")
class AutenticacaoRequestDtoValidacaoTest {

    private static ValidatorFactory factory;
    private Validator validator;

    @BeforeAll
    static void init() {
        factory = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    static void close() {
        factory.close();
    }

    @BeforeEach
    void setUp() {
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve aceitar credenciais válidas")
    void deveAceitarCredenciaisValidas() {
        AutenticacaoRequestDto dto = new AutenticacaoRequestDto(
                "breno@megusta.com", "Senha@123"
        );

        Set<ConstraintViolation<AutenticacaoRequestDto>> violacoes = validator.validate(dto);

        assertTrue(violacoes.isEmpty());
    }

    @Test
    @DisplayName("Deve recusar login com formato inválido (A07)")
    void deveRecusarLoginInvalido() {
        AutenticacaoRequestDto dto = new AutenticacaoRequestDto(
                "nao-eh-email", "Senha@123"
        );

        Set<ConstraintViolation<AutenticacaoRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    @DisplayName("Deve recusar login vazio")
    void deveRecusarLoginVazio() {
        AutenticacaoRequestDto dto = new AutenticacaoRequestDto("", "Senha@123");

        Set<ConstraintViolation<AutenticacaoRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
    }

    @Test
    @DisplayName("Deve recusar senha vazia")
    void deveRecusarSenhaVazia() {
        AutenticacaoRequestDto dto = new AutenticacaoRequestDto(
                "breno@megusta.com", ""
        );

        Set<ConstraintViolation<AutenticacaoRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }
}
