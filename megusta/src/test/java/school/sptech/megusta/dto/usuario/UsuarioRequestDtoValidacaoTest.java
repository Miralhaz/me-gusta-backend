package school.sptech.megusta.dto.usuario;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de validação do UsuarioRequestDto (A07 - senha fraca, telefone obrigatório)")
class UsuarioRequestDtoValidacaoTest {

    private static final String TELEFONE_VALIDO = "11999999999";

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

    private boolean violaTelefone(UsuarioRequestDto dto) {
        return validator.validate(dto).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefone"));
    }

    private boolean violaCampo(UsuarioRequestDto dto, String campo) {
        return validator.validate(dto).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(campo));
    }

    @Test
    @DisplayName("Deve aceitar cadastro com senha forte válida")
    void deveAceitarSenhaForte() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Senha@123", "breno@megusta.com", TELEFONE_VALIDO);

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
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Ab@1", "breno@megusta.com", TELEFONE_VALIDO);

        Set<ConstraintViolation<UsuarioRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violaCampo(dto, "senha"));
    }

    @Test
    @DisplayName("Deve recusar senha sem caractere especial (A07)")
    void deveRecusarSenhaSemEspecial() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Senha123", "breno@megusta.com", TELEFONE_VALIDO);

        Set<ConstraintViolation<UsuarioRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violaCampo(dto, "senha"));
    }

    @Test
    @DisplayName("Deve recusar senha sem letra maiúscula (A07)")
    void deveRecusarSenhaSemMaiuscula() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "senha@123", "breno@megusta.com", TELEFONE_VALIDO);

        Set<ConstraintViolation<UsuarioRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violaCampo(dto, "senha"));
    }

    @Test
    @DisplayName("Deve recusar senha sem número (A07)")
    void deveRecusarSenhaSemNumero() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Senha@abc", "breno@megusta.com", TELEFONE_VALIDO);

        Set<ConstraintViolation<UsuarioRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violaCampo(dto, "senha"));
    }

    @Test
    @DisplayName("Deve recusar e-mail inválido")
    void deveRecusarEmailInvalido() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Senha@123", "nao-eh-email", TELEFONE_VALIDO);

        Set<ConstraintViolation<UsuarioRequestDto>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(violaCampo(dto, "email"));
    }

    // --- Telefone ---

    @Test
    @DisplayName("Deve aceitar telefone válido com 11 dígitos")
    void deveAceitarTelefoneCom11Digitos() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Senha@123", "breno@megusta.com", "11999999999");

        assertFalse(violaTelefone(dto));
    }

    @Test
    @DisplayName("Deve aceitar telefone válido com 10 dígitos")
    void deveAceitarTelefoneCom10Digitos() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Senha@123", "breno@megusta.com", "1199999999");

        assertFalse(violaTelefone(dto));
    }

    @Test
    @DisplayName("Deve recusar telefone ausente")
    void deveRecusarTelefoneAusente() {
        UsuarioRequestDto dto = new UsuarioRequestDto("Breno", "Senha@123", "breno@megusta.com", null);

        assertTrue(violaTelefone(dto));
    }

    @Test
    @DisplayName("Deve recusar telefone vazio ou apenas com espaços")
    void deveRecusarTelefoneVazio() {
        assertTrue(violaTelefone(new UsuarioRequestDto("Breno", "Senha@123", "breno@megusta.com", "")));
        assertTrue(violaTelefone(new UsuarioRequestDto("Breno", "Senha@123", "breno@megusta.com", "   ")));
    }

    @Test
    @DisplayName("Deve recusar telefone com máscara")
    void deveRecusarTelefoneComMascara() {
        assertTrue(violaTelefone(new UsuarioRequestDto("Breno", "Senha@123", "breno@megusta.com", "(11) 99999-9999")));
        assertTrue(violaTelefone(new UsuarioRequestDto("Breno", "Senha@123", "breno@megusta.com", "11 99999 9999")));
    }

    @Test
    @DisplayName("Deve recusar telefone com letras")
    void deveRecusarTelefoneComLetras() {
        assertTrue(violaTelefone(new UsuarioRequestDto("Breno", "Senha@123", "breno@megusta.com", "1199999abc9")));
    }

    @Test
    @DisplayName("Deve recusar telefone com menos de 10 dígitos")
    void deveRecusarTelefoneCurto() {
        assertTrue(violaTelefone(new UsuarioRequestDto("Breno", "Senha@123", "breno@megusta.com", "119999999")));
    }

    @Test
    @DisplayName("Deve recusar telefone com mais de 11 dígitos")
    void deveRecusarTelefoneLongo() {
        assertTrue(violaTelefone(new UsuarioRequestDto("Breno", "Senha@123", "breno@megusta.com", "119999999999")));
    }
}
