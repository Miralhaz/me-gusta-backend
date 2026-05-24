package school.sptech.megusta.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.UnidadeMedida;
import school.sptech.megusta.repository.UnidadeMedidaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de UnidadeMedidaService")
class UnidadeMedidaServiceTest {

    @Mock
    private UnidadeMedidaRepository unidadeMedidaRepository;

    @InjectMocks
    private UnidadeMedidaService unidadeMedidaService;

    @Nested
    @DisplayName("Método listar")
    class listar {

        @Test
        @DisplayName("Deve listar corretamente")
        void deveListarCorretamente() {
            UnidadeMedida unidadeMedida = new UnidadeMedida();
            List<UnidadeMedida> unidadeMedidaLista = new ArrayList<>();
            unidadeMedidaLista.add(unidadeMedida);

            Mockito.when(unidadeMedidaRepository.findAll())
                    .thenReturn(unidadeMedidaLista);

            Assertions.assertEquals(unidadeMedidaLista, unidadeMedidaService.listar());
        }

        @Test
        @DisplayName("Deve retornar lista vazia")
        void deveRetornarListaVazia() {
            List<UnidadeMedida> unidadeMedidaLista = new ArrayList<>();

            Mockito.when(unidadeMedidaRepository.findAll())
                    .thenReturn(unidadeMedidaLista);

            Assertions.assertEquals(unidadeMedidaLista, unidadeMedidaService.listar());
        }
    }

    @Nested
    @DisplayName("Método buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("Deve buscar corretamente")
        void deveBuscarCorretamente() {
            Integer id = 1;
            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(id);

            Mockito.when(unidadeMedidaRepository.findById(id))
                    .thenReturn(Optional.of(unidadeMedida));

            Assertions.assertEquals(unidadeMedida,
                    unidadeMedidaService.buscarPorId(id));
        }

        @Test
        @DisplayName("Deve lançar exception caso unidade não encontrada")
        void deveLancarExceptionCasoUnidadeNaoEncontrada() {
            Integer id = 1;

            Mockito.when(unidadeMedidaRepository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> unidadeMedidaService.buscarPorId(id));
        }
    }

    @Nested
    @DisplayName("Método cadastrar")
    class cadastrar {

        @Test
        @DisplayName("Deve cadastrar corretamente")
        void deveCadastrarCorretamente() {
            String unidade = "KG";
            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setUnidade(unidade);

            Mockito.when(unidadeMedidaRepository.existsByUnidade(unidade))
                    .thenReturn(false);

            Mockito.when(unidadeMedidaRepository.save(unidadeMedida))
                    .thenReturn(unidadeMedida);

            Assertions.assertEquals(unidadeMedida,
                    unidadeMedidaService.cadastrar(unidadeMedida));
        }

        @Test
        @DisplayName("Deve lançar exception caso unidade já exista")
        void deveLancarExceptionCasoUnidadeJaExista() {
            String unidade = "KG";
            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setUnidade(unidade);

            Mockito.when(unidadeMedidaRepository.existsByUnidade(unidade))
                    .thenReturn(true);

            Assertions.assertThrows(RecursoConflitoException.class,
                    () -> unidadeMedidaService.cadastrar(unidadeMedida));
        }
    }

    @Nested
    @DisplayName("Método atualizar")
    class atualizar {

        @Test
        @DisplayName("Deve atualizar corretamente")
        void deveAtualizarCorretamente() {
            Integer id = 1;
            String unidade = "KG";
            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(id);
            unidadeMedida.setUnidade(unidade);

            Mockito.when(unidadeMedidaRepository.findById(id))
                    .thenReturn(Optional.of(unidadeMedida));

            Mockito.when(unidadeMedidaRepository.save(Mockito.any(UnidadeMedida.class)))
                    .thenReturn(unidadeMedida);

            Assertions.assertEquals(unidadeMedida,
                    unidadeMedidaService.atualizar(unidadeMedida, id));
        }

        @Test
        @DisplayName("Deve lançar exception caso unidade não encontrada")
        void deveLancarExceptionCasoUnidadeNaoEncontrada() {
            Integer id = 1;
            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(id);

            Mockito.when(unidadeMedidaRepository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> unidadeMedidaService.atualizar(unidadeMedida, id));
        }
    }

    @Nested
    @DisplayName("Método deletar")
    class deletar {

        @Test
        @DisplayName("Deve deletar corretamente")
        void deveDeletarCorretamente() {
            Integer id = 1;
            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(id);

            Mockito.when(unidadeMedidaRepository.findById(id))
                    .thenReturn(Optional.of(unidadeMedida));

            unidadeMedidaService.deletar(id);

            Mockito.verify(unidadeMedidaRepository, Mockito.times(1))
                    .deleteById(id);
        }

        @Test
        @DisplayName("Deve lançar exception caso unidade não encontrada")
        void deveLancarExceptionCasoUnidadeNaoEncontrada() {
            Integer id = 1;
            Mockito.when(unidadeMedidaRepository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> unidadeMedidaService.deletar(id));
        }
    }
}