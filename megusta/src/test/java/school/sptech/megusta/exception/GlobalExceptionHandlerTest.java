package school.sptech.megusta.exception;

import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DisplayName("Testes de GlobalExceptionHandler (A05 - Security Misconfiguration)")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("Erros tratados")
    class errosTratados {

        @Test
        @DisplayName("Deve retornar 404 para RecursoNaoEncontradoException")
        void deveRetornar404ParaRecursoNaoEncontrado() {
            ResponseEntity<String> resposta = handler.handleNaoEncontrado(
                    new RecursoNaoEncontradoException("Fogazza não encontrada.")
            );

            Assertions.assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
            Assertions.assertEquals("Fogazza não encontrada.", resposta.getBody());
        }

        @Test
        @DisplayName("Deve retornar 409 para RecursoConflitoException")
        void deveRetornar409ParaRecursoConflito() {
            ResponseEntity<String> resposta = handler.handleConflito(
                    new RecursoConflitoException("Usuário já existe!")
            );

            Assertions.assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode());
            Assertions.assertEquals("Usuário já existe!", resposta.getBody());
        }

        @Test
        @DisplayName("Deve retornar 403 para AcessoNegadoException")
        void deveRetornar403ParaAcessoNegado() {
            ResponseEntity<String> resposta = handler.handleAcessoNegado(
                    new AcessoNegadoException("Sem permissão para alterar este usuário")
            );

            Assertions.assertEquals(HttpStatus.FORBIDDEN, resposta.getStatusCode());
            Assertions.assertEquals("Sem permissão para alterar este usuário", resposta.getBody());
        }
    }

    @Nested
    @DisplayName("Erros genéricos (Information Disclosure)")
    class errosGenericos {

        @Test
        @DisplayName("Deve retornar mensagem genérica sem expor detalhes internos (A05)")
        void deveOcultarMensagensInternas() {
            // Simula um NullPointerException que antes vazaria a mensagem interna
            ResponseEntity<String> resposta = handler.handleGenerico(
                    new NullPointerException("Cannot invoke usuario.getId() because usuario is null")
            );

            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
            Assertions.assertEquals("Erro interno do servidor", resposta.getBody());

            // Garante que o detalhe da exceção NÃO está na resposta
            Assertions.assertFalse(
                    resposta.getBody().contains("usuario is null"),
                    "A resposta não deve conter detalhes internos da exceção"
            );
        }

        @Test
        @DisplayName("Deve retornar mensagem genérica mesmo para SQLException simulada (A05)")
        void deveOcultarMensagensDeBanco() {
            // Simula erro que antes vazaria detalhes do banco de dados
            String mensagemSqlInterna =
                    "SQLSyntaxErrorException: Table 'megusta.usuaro' doesn't exist";

            ResponseEntity<String> resposta = handler.handleGenerico(
                    new RuntimeException(mensagemSqlInterna)
            );

            Assertions.assertEquals("Erro interno do servidor", resposta.getBody());
            Assertions.assertFalse(
                    resposta.getBody().contains("SQLSyntaxErrorException"),
                    "A resposta não deve vazar detalhes do banco de dados"
            );
            Assertions.assertFalse(
                    resposta.getBody().contains("megusta.usuaro"),
                    "A resposta não deve vazar nomes de tabelas"
            );
        }
    }
}
