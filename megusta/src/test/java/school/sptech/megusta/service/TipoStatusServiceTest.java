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
import school.sptech.megusta.model.TipoStatus;
import school.sptech.megusta.repository.TipoStatusRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Tipo Status")
class TipoStatusServiceTest {

    @Mock
    private TipoStatusRepository tipoStatusRepository;

    @InjectMocks
    private TipoStatusService tipoStatusService;

    @Nested
    @DisplayName("Método Listar")
    class listar {

        @Test
        @DisplayName("Deve listar corretamente")
        void deveListarCorretamente() {
            TipoStatus tipoStatus = new TipoStatus();
            List<TipoStatus> tipoStatusList = new ArrayList<>();

            tipoStatusList.add(tipoStatus);

            Mockito.when(tipoStatusRepository.findAll()).thenReturn(tipoStatusList);

            Assertions.assertEquals(tipoStatusList, tipoStatusService.listar());
        }

        @Test
        @DisplayName("Deve retornar lista vazia")
        void deveRetornarListaVazia() {

            List<TipoStatus> tipoStatusList = new ArrayList<>();

            Mockito.when(tipoStatusRepository.findAll()).thenReturn(tipoStatusList);

            Assertions.assertEquals(tipoStatusList, tipoStatusService.listar());
        }

    }

    @Nested
    @DisplayName("Método buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("Deve buscar corretamente")
        void deveBuscarCorretamente() {
            Integer id = 1;
            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(id);

            Mockito.when(tipoStatusRepository.findById(id))
                    .thenReturn(Optional.of(tipoStatus));

            Assertions.assertEquals(tipoStatus,
                    tipoStatusService.buscarPorId(id));
        }

        @Test
        @DisplayName("Deve lançar exception caso status não encontrado")
        void deveRetornarExceptionCasoStatusNaoEncontrado() {
            Integer id = 1;

            Mockito.when(tipoStatusRepository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> tipoStatusService.buscarPorId(id));
        }
    }

    @Nested
    @DisplayName("Método cadastrar")
    class cadastrar {

        @Test
        @DisplayName("Deve cadastrar corretamente")
        void deveCadastrarCorretamente() {
            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setNome("ATIVO");

            Mockito.when(tipoStatusRepository.existsByNomeIgnoreCase(tipoStatus.getNome()))
                    .thenReturn(false);

            Mockito.when(tipoStatusRepository.save(tipoStatus))
                    .thenReturn(tipoStatus);

            Assertions.assertEquals(tipoStatus, tipoStatusService.cadastrar(tipoStatus));
        }

        @Test
        @DisplayName("Deve lançar exception caso status já exista")
        void deveLancarExceptionCasoStatusJaExista() {
            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setNome("ATIVO");

            Mockito.when(tipoStatusRepository.existsByNomeIgnoreCase(tipoStatus.getNome()))
                    .thenReturn(true);

            Assertions.assertThrows(RecursoConflitoException.class,
                    () -> tipoStatusService.cadastrar(tipoStatus));
        }
    }

    @Nested
    @DisplayName("Método atualizar")
    class atualizar {

        @Test
        @DisplayName("Deve atualizar corretamente")
        void deveAtualizarCorretamente() {
            Integer id = 1;
            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(id);
            tipoStatus.setNome("ATIVO");

            Mockito.when(tipoStatusRepository.findById(id))
                    .thenReturn(Optional.of(tipoStatus));

            Mockito.when(tipoStatusRepository.save(Mockito.any(TipoStatus.class)))
                    .thenReturn(tipoStatus);

            Assertions.assertEquals(tipoStatus,
                    tipoStatusService.atualizar(tipoStatus.getId(), tipoStatus));
        }

        @Test
        @DisplayName("Deve lançar exception caso status não encontrado")
        void deveLancarExceptionCasoStatusNaoEncontrado() {
            Integer id = 1;
            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(id);

            Mockito.when(tipoStatusRepository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> tipoStatusService.atualizar(tipoStatus.getId(), tipoStatus));
        }

        @Test
        @DisplayName("Deve lançar exception caso status já exista")
        void deveLancarExceptionCasoStatusJaExista() {
            Integer id = 1;
            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(id);
            tipoStatus.setNome("ATIVO");

            Mockito.when(tipoStatusRepository.findById(id))
                    .thenReturn(Optional.of(tipoStatus));

            Mockito.when(tipoStatusRepository.existsByNomeAndIdNot(tipoStatus.getNome(), id))
                    .thenReturn(true);

            Assertions.assertThrows(RecursoConflitoException.class,
                    () -> tipoStatusService.atualizar(id, tipoStatus));
        }
    }

    @Nested
    @DisplayName("Método excluir")
    class excluir {

        @Test
        @DisplayName("Deve excluir corretamente")
        void deveExcluirCorretamente() {
            Integer id = 1;
            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(id);

            Mockito.when(tipoStatusRepository.findById(id))
                    .thenReturn(Optional.of(tipoStatus));

            tipoStatusService.excluir(tipoStatus.getId());

            Mockito.verify(tipoStatusRepository, Mockito.times(1))
                    .deleteById(tipoStatus.getId());
        }

        @Test
        @DisplayName("Deve lançar exception caso status não encontrado")
        void deveLancarExceptionCasoStatusNaoEncontrado() {
            Integer id = 1;
            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(id);

            Mockito.when(tipoStatusRepository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> tipoStatusService.excluir(tipoStatus.getId()));
        }
    }
}