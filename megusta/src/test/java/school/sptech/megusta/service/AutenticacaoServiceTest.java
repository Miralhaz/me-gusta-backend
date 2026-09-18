package school.sptech.megusta.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de AutenticacaoService")
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Nested
    @DisplayName("Método loadUserByUsername")
    class loadUserByUsername {

        @Test
        @DisplayName("Deve retornar o usuário quando o e-mail existe")
        void deveRetornarUsuarioQuandoEmailExiste() {
            Usuario usuario = new Usuario(1, "Enrico", "enrico@email.com", "123456");

            Mockito.when(repository.findByEmail("enrico@email.com"))
                    .thenReturn(usuario);

            UserDetails resultado = autenticacaoService.loadUserByUsername("enrico@email.com");

            Assertions.assertNotNull(resultado);
            Assertions.assertEquals("enrico@email.com", resultado.getUsername());
            Mockito.verify(repository).findByEmail("enrico@email.com");
        }

        @Test
        @DisplayName("Deve lançar UsernameNotFoundException quando o e-mail não existe")
        void deveLancarUsernameNotFoundQuandoEmailNaoExiste() {
            Mockito.when(repository.findByEmail("nao-existe@email.com"))
                    .thenReturn(null);

            UsernameNotFoundException excecao = Assertions.assertThrows(
                    UsernameNotFoundException.class,
                    () -> autenticacaoService.loadUserByUsername("nao-existe@email.com")
            );

            Assertions.assertEquals("Credenciais inválidas", excecao.getMessage());
            Mockito.verify(repository).findByEmail("nao-existe@email.com");
        }
    }
}