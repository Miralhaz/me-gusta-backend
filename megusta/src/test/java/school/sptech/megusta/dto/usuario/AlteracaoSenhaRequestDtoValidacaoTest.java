package school.sptech.megusta.dto.usuario;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de validação do AlteracaoSenhaRequestDto")
class AlteracaoSenhaRequestDtoValidacaoTest {

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
    @DisplayName("Deve aceitar e-mail válido e senha forte")
    void deveAceitarPayloadValido() {
        AlteracaoSenhaRequestDto dto = new AlteracaoSenhaRequestDto(
                "breno@megusta.com", "Senha@123");

        Set<ConstraintViolation<AlteracaoSenhaRequestDto>> violacoes = validator.validate(dto);

        assertTrue(violacoes.isEmpty(), () ->
                "Não deveria haver violações, mas houve: " +
                        violacoes.stream()
                                .map(v -> v.getPropertyPath() + " - " + v.getMessage())
                                .collect(Collectors.joining("; "))
        );
    }

    @Test
    @DisplayName("Deve recusar e-mail ausente")
    void deveRecusarEmailAusente() {
        AlteracaoSenhaRequestDto dto = new AlteracaoSenhaRequestDto(null, "Senha@123");

        Set<ConstraintViolation<AlteracaoSenhaRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Deve recusar e-mail em branco")
    void deveRecusarEmailEmBranco() {
        AlteracaoSenhaRequestDto dto = new AlteracaoSenhaRequestDto("   ", "Senha@123");

        Set<ConstraintViolation<AlteracaoSenhaRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Deve recusar senha ausente")
    void deveRecusarSenhaAusente() {
        AlteracaoSenhaRequestDto dto = new AlteracaoSenhaRequestDto("breno@megusta.com", null);

        Set<ConstraintViolation<AlteracaoSenhaRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("novaSenha")));
    }

    @Test
    @DisplayName("Deve recusar senha em branco")
    void deveRecusarSenhaEmBranco() {
        AlteracaoSenhaRequestDto dto = new AlteracaoSenhaRequestDto("breno@megusta.com", "   ");

        Set<ConstraintViolation<AlteracaoSenhaRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("novaSenha")));
    }

    @Test
    @DisplayName("Deve recusar e-mail inválido")
    void deveRecusarEmailInvalido() {
        AlteracaoSenhaRequestDto dto = new AlteracaoSenhaRequestDto(
                "nao-eh-email", "Senha@123");

        Set<ConstraintViolation<AlteracaoSenhaRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Deve recusar senha muito curta")
    void deveRecusarSenhaCurta() {
        AlteracaoSenhaRequestDto dto = new AlteracaoSenhaRequestDto(
                "breno@megusta.com", "Ab@1");

        Set<ConstraintViolation<AlteracaoSenhaRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("novaSenha")));
    }

    @Test
    @DisplayName("Deve recusar senha sem letra maiúscula")
    void deveRecusarSenhaSemMaiuscula() {
        AlteracaoSenhaRequestDto dto = new AlteracaoSenhaRequestDto(
                "breno@megusta.com", "senha@123");

        Set<ConstraintViolation<AlteracaoSenhaRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("novaSenha")));
    }

    @Test
    @DisplayName("Deve recusar senha sem letra minúscula")
    void deveRecusarSenhaSemMinuscula() {
        AlteracaoSenhaRequestDto dto = new AlteracaoSenhaRequestDto(
                "breno@megusta.com", "SENHA@123");

        Set<ConstraintViolation<AlteracaoSenhaRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("novaSenha")));
    }

    @Test
    @DisplayName("Deve recusar senha sem número")
    void deveRecusarSenhaSemNumero() {
        AlteracaoSenhaRequestDto dto = new AlteracaoSenhaRequestDto(
                "breno@megusta.com", "Senha@abc");

        Set<ConstraintViolation<AlteracaoSenhaRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("novaSenha")));
    }

    @Test
    @DisplayName("Deve recusar senha sem caractere especial")
    void deveRecusarSenhaSemEspecial() {
        AlteracaoSenhaRequestDto dto = new AlteracaoSenhaRequestDto(
                "breno@megusta.com", "Senha123");

        Set<ConstraintViolation<AlteracaoSenhaRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("novaSenha")));
    }
}
