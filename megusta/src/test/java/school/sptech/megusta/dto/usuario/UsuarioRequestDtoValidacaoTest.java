package school.sptech.megusta.dto.usuario;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de validação do UsuarioRequestDto (A07 - senha fraca)")
class UsuarioRequestDtoValidacaoTest {

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
    @DisplayName("Deve aceitar cadastro com senha forte válida")
    void deveAceitarSenhaForte() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Senha@123", "breno@megusta.com");

        Set<ConstraintViolation<UsuarioRequestDto>> violacoes = validator.validate(dto);

        assertTrue(violacoes.isEmpty(), () ->
                "Não deveria haver violações, mas houve: " +
                        violacoes.stream()
                                .map(v -> v.getPropertyPath() + " - " + v.getMessage())
                                .collect(Collectors.joining("; "))
        );
    }

    @Test
    @DisplayName("Deve recusar senha muito curta (A07)")
    void deveRecusarSenhaCurta() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Ab@1", "breno@megusta.com");

        Set<ConstraintViolation<UsuarioRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }

    @Test
    @DisplayName("Deve recusar senha sem caractere especial (A07)")
    void deveRecusarSenhaSemEspecial() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Senha123", "breno@megusta.com");

        Set<ConstraintViolation<UsuarioRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }

    @Test
    @DisplayName("Deve recusar senha sem letra maiúscula (A07)")
    void deveRecusarSenhaSemMaiuscula() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "senha@123", "breno@megusta.com");

        Set<ConstraintViolation<UsuarioRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }

    @Test
    @DisplayName("Deve recusar senha sem número (A07)")
    void deveRecusarSenhaSemNumero() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Senha@abc", "breno@megusta.com");

        Set<ConstraintViolation<UsuarioRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }

    @Test
    @DisplayName("Deve recusar e-mail inválido")
    void deveRecusarEmailInvalido() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Senha@123", "nao-eh-email");

        Set<ConstraintViolation<UsuarioRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violacoes.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }
}
