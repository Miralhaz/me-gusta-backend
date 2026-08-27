package school.sptech.megusta.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import school.sptech.megusta.dto.usuario.UsuarioRequestDto;
import school.sptech.megusta.dto.usuario.UsuarioUpdateDto;
import school.sptech.megusta.exception.AcessoNegadoException;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.mapper.UsuarioMapper;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                usuarioService,
                "passwordEncoder",
                passwordEncoder
        );
    }


    @Nested
    @DisplayName("Método listar")
    class listar {

        @Test
        @DisplayName("Deve listar corretamente")
        void deveListarCorretamente() {

            List<Usuario> lista = new ArrayList<>();
            lista.add(new Usuario());

            Mockito.when(repository.findAll())
                    .thenReturn(lista);

            Assertions.assertEquals(lista,
                    usuarioService.listar());
        }

        @Test
        @DisplayName("Deve retornar lista vazia")
        void deveRetornarListaVazia() {

            List<Usuario> lista = new ArrayList<>();

            Mockito.when(repository.findAll())
                    .thenReturn(lista);

            Assertions.assertEquals(lista,
                    usuarioService.listar());
        }
    }

    @Nested
    @DisplayName("Método buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("Deve buscar corretamente")
        void deveBuscarCorretamente() {

            Integer id = 1;

            Usuario usuario = new Usuario();
            usuario.setId(id);

            Mockito.when(repository.findById(id))
                    .thenReturn(Optional.of(usuario));

            Assertions.assertEquals(usuario,
                    usuarioService.buscarPorId(id));
        }

        @Test
        @DisplayName("Deve lançar exception quando usuário não existir")
        void deveLancarExceptionQuandoUsuarioNaoExistir() {

            Integer id = 1;

            Mockito.when(repository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(
                    RecursoNaoEncontradoException.class,
                    () -> usuarioService.buscarPorId(id)
            );
        }
    }

    @Nested
    @DisplayName("Método cadastrar")
    class cadastrar {

        @Test
        @DisplayName("Deve cadastrar corretamente")
        void deveCadastrarCorretamente() {

            UsuarioRequestDto dto = new UsuarioRequestDto();
            dto.setNome("Bianca");
            dto.setEmail("bi@teste.com");
            dto.setSenha("123");

            Usuario usuario = UsuarioMapper.toEntity(dto);

            Mockito.when(passwordEncoder.encode("123"))
                    .thenReturn("senhaCriptografada");

            Mockito.when(repository.existsByNomeAndEmail(
                            "Bianca",
                            "bi@teste.com"))
                    .thenReturn(false);

            Mockito.when(repository.save(Mockito.any(Usuario.class)))
                    .thenReturn(usuario);

            Assertions.assertNotNull(
                    usuarioService.cadastrar(dto));
        }

        @Test
        @DisplayName("Deve lançar exception quando usuário já existir")
        void deveLancarExceptionQuandoUsuarioJaExistir() {

            UsuarioRequestDto dto = new UsuarioRequestDto();
            dto.setNome("Bianca");
            dto.setEmail("bi@teste.com");
            dto.setSenha("123");

            Mockito.when(passwordEncoder.encode("123"))
                    .thenReturn("senhaCriptografada");

            Mockito.when(repository.existsByNomeAndEmail(
                            "Bianca",
                            "bi@teste.com"))
                    .thenReturn(true);

            Assertions.assertThrows(
                    RecursoConflitoException.class,
                    () -> usuarioService.cadastrar(dto)
            );
        }
    }

    @Nested
    @DisplayName("Método atualizar")
    class atualizar {

        @Test
        @DisplayName("Deve atualizar corretamente quando o id da URL pertence ao usuário autenticado")
        void deveAtualizarCorretamente() {

            Integer id = 1;
            Integer idAutenticado = 1;

            Usuario existente = new Usuario(id, "Bianca", "bi@teste.com", "hashAntigo");

            UsuarioUpdateDto dto = new UsuarioUpdateDto();
            dto.setNome("Bianca Souza");
            dto.setEmail("bi.nova@teste.com");

            Mockito.when(repository.findById(id))
                    .thenReturn(Optional.of(existente));

            Mockito.when(
                            repository.existsByNomeAndEmailAndIdNot(
                                    "Bianca Souza",
                                    "bi.nova@teste.com",
                                    id))
                    .thenReturn(false);

            Mockito.when(repository.save(Mockito.any(Usuario.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            var resposta = usuarioService.atualizar(dto, id, idAutenticado);

            Assertions.assertNotNull(resposta);
            Assertions.assertEquals("Bianca Souza", resposta.getNome());
            Assertions.assertEquals("bi.nova@teste.com", resposta.getEmail());
            Assertions.assertEquals("hashAntigo", existente.getSenha());
            Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
        }

        @Test
        @DisplayName("Deve lançar AcessoNegadoException quando o id da URL for de outro usuário (IDOR)")
        void deveLancarAcessoNegadoQuandoIdDiferenteDoAutenticado() {

            Integer id = 1;
            Integer idAutenticado = 2;

            UsuarioUpdateDto dto = new UsuarioUpdateDto();
            dto.setNome("Hacker");
            dto.setEmail("hacker@gmail.com");

            Assertions.assertThrows(
                    AcessoNegadoException.class,
                    () -> usuarioService.atualizar(dto, id, idAutenticado)
            );

            Mockito.verify(repository, Mockito.never()).findById(Mockito.any());
            Mockito.verify(repository, Mockito.never()).save(Mockito.any(Usuario.class));
        }

        @Test
        @DisplayName("Deve lançar exception quando usuário não existir")
        void deveLancarExceptionQuandoUsuarioNaoExistir() {

            Integer id = 1;
            Integer idAutenticado = 1;

            UsuarioUpdateDto dto = new UsuarioUpdateDto();
            dto.setNome("Bianca");
            dto.setEmail("bi@teste.com");

            Mockito.when(repository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(
                    RecursoNaoEncontradoException.class,
                    () -> usuarioService.atualizar(dto, id, idAutenticado)
            );
        }

        @Test
        @DisplayName("Deve lançar exception quando existir duplicidade")
        void deveLancarExceptionQuandoExistirDuplicidade() {

            Integer id = 1;
            Integer idAutenticado = 1;

            Usuario existente = new Usuario(id, "Bianca", "bi@teste.com", "hashAntigo");

            UsuarioUpdateDto dto = new UsuarioUpdateDto();
            dto.setNome("Bianca");
            dto.setEmail("bi@teste.com");

            Mockito.when(repository.findById(id))
                    .thenReturn(Optional.of(existente));

            Mockito.when(
                            repository.existsByNomeAndEmailAndIdNot(
                                    "Bianca",
                                    "bi@teste.com",
                                    id))
                    .thenReturn(true);

            Assertions.assertThrows(
                    RecursoConflitoException.class,
                    () -> usuarioService.atualizar(dto, id, idAutenticado)
            );
        }
    }

    @Nested
    @DisplayName("Método excluir")
    class excluir {

        @Test
        @DisplayName("Deve excluir corretamente quando o id da URL pertence ao usuário autenticado")
        void deveExcluirCorretamente() {

            Integer id = 1;
            Integer idAutenticado = 1;

            Mockito.when(repository.existsById(id))
                    .thenReturn(true);

            usuarioService.excluir(id, idAutenticado);

            Mockito.verify(repository,
                            Mockito.times(1))
                    .deleteById(id);
        }

        @Test
        @DisplayName("Deve lançar AcessoNegadoException quando o id da URL for de outro usuário (IDOR)")
        void deveLancarAcessoNegadoQuandoIdDiferenteDoAutenticado() {

            Integer id = 1;
            Integer idAutenticado = 2;

            Assertions.assertThrows(
                    AcessoNegadoException.class,
                    () -> usuarioService.excluir(id, idAutenticado)
            );

            Mockito.verify(repository, Mockito.never()).deleteById(Mockito.any());
        }

        @Test
        @DisplayName("Deve lançar exception quando usuário não existir")
        void deveLancarExceptionQuandoUsuarioNaoExistir() {

            Integer id = 1;
            Integer idAutenticado = 1;

            Mockito.when(repository.existsById(id))
                    .thenReturn(false);

            Assertions.assertThrows(
                    RecursoNaoEncontradoException.class,
                    () -> usuarioService.excluir(id, idAutenticado)
            );
        }
    }

}
