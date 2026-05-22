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
import school.sptech.megusta.model.CategoriaFogazza;
import school.sptech.megusta.model.Fogazzas;
import school.sptech.megusta.repository.CategoriaFogazzaRepository;
import school.sptech.megusta.repository.FogazzasRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da FogazzaService")
class FogazzasServiceTest {

    @Mock
    private FogazzasRepository fogazzasRepository;

    @Mock
    private CategoriaFogazzaRepository categoriaFogazzaRepository;

    @InjectMocks
    private FogazzasService fogazzasService;

    @Nested
    @DisplayName("Método listar")
    class listar {

        @Test
        @DisplayName("Deve listar corretamente")
        void deveListarCorretamente(){
            Fogazzas fogazzas = new Fogazzas();
            List<Fogazzas> fogazzasList = new ArrayList<>();

            fogazzasList.add(fogazzas);

            Mockito.when(fogazzasRepository.findAll()).thenReturn(fogazzasList);

            Assertions.assertEquals(fogazzasList, fogazzasService.listar());
        }

        @Test
        @DisplayName("Deve retornar lista vazia")
        void deveRetornarListaVazia(){
            List<Fogazzas> fogazzasList = new ArrayList<>();

            Mockito.when(fogazzasRepository.findAll()).thenReturn(fogazzasList);

            Assertions.assertEquals(fogazzasList, fogazzasService.listar());
        }
    }

    @Nested
    @DisplayName("Método buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("deve buscar corretamente")
        void deveBuscarCorretamente(){
            Fogazzas fogazzas = new Fogazzas();
            fogazzas.setId(1);

            Mockito.when(fogazzasRepository.findById(fogazzas.getId())).thenReturn(Optional.of(fogazzas));

            Assertions.assertEquals(fogazzas, fogazzasService.buscarPorId(fogazzas.getId()));
        }

        @Test
        @DisplayName("deve Lancar exception caso fogazza não encontrada")
        void deveLancarExceptionCasoFogazzaNaoEncontrada(){
            Fogazzas fogazzas = new Fogazzas();
            fogazzas.setId(1);

            Mockito.when(fogazzasRepository.findById(fogazzas.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> fogazzasService.buscarPorId(fogazzas.getId()));
        }
    }

    @Nested
    @DisplayName("Método cadastrar")
    class cadastrar {

        @Test
        @DisplayName("deve cadastrar corretamente")
        void deveCadastrarCorretamente(){
            CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
            categoriaFogazza.setId(1);

            Fogazzas fogazzas = new Fogazzas();
            fogazzas.setNome("queijo");
            fogazzas.setCategoriaFogazza(categoriaFogazza);

            Mockito.when(fogazzasRepository.existsByNome(fogazzas.getNome())).thenReturn(false);
            Mockito.when(categoriaFogazzaRepository.findById(categoriaFogazza.getId())).thenReturn(Optional.of(categoriaFogazza));
            Mockito.when(fogazzasRepository.save(fogazzas)).thenReturn(fogazzas);

            Assertions.assertEquals(fogazzas, fogazzasService.cadastrar(fogazzas, categoriaFogazza.getId()));
        }

        @Test
        @DisplayName("deve Lancar exception caso categoriaFogazza não encontrada")
        void deveLancarExceptionCasoCategoriaFogazzaNaoEncontrada(){
            CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
            categoriaFogazza.setId(1);

            Fogazzas fogazzas = new Fogazzas();
            fogazzas.setNome("queijo");
            fogazzas.setCategoriaFogazza(categoriaFogazza);

            Mockito.when(fogazzasRepository.existsByNome(fogazzas.getNome())).thenReturn(false);
            Mockito.when(categoriaFogazzaRepository.findById(categoriaFogazza.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> fogazzasService.cadastrar(fogazzas, categoriaFogazza.getId()));
        }

        @Test
        @DisplayName("deve lançar exception caso fogazza não encontrada")
        void deveLancarExceptionCasoFogazzaNaoEncontrada(){
            CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
            categoriaFogazza.setId(1);

            Fogazzas fogazzas = new Fogazzas();
            fogazzas.setNome("queijo");
            fogazzas.setCategoriaFogazza(categoriaFogazza);

            Mockito.when(fogazzasRepository.existsByNome(fogazzas.getNome())).thenReturn(true);

            Assertions.assertThrows(RecursoConflitoException.class, () -> fogazzasService.cadastrar(fogazzas, categoriaFogazza.getId()));
        }
    }

    @Nested
    @DisplayName("Método atualizar")
    class atualizar {

        @Test
        @DisplayName("deve atualizar corretamente")
        void deveAtualizarCorretamente(){
            CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
            categoriaFogazza.setId(1);

            Fogazzas fogazzas = new Fogazzas();
            fogazzas.setNome("queijo");
            fogazzas.setCategoriaFogazza(categoriaFogazza);
            fogazzas.setId(1);

            Mockito.when(fogazzasRepository.findById(fogazzas.getId())).thenReturn(Optional.of(fogazzas));
            Mockito.when(categoriaFogazzaRepository.findById(categoriaFogazza.getId())).thenReturn(Optional.of(categoriaFogazza));
            Mockito.when(fogazzasRepository.save(fogazzas)).thenReturn(fogazzas);

            Assertions.assertEquals(fogazzas, fogazzasService.atualizar(fogazzas.getId(), fogazzas, categoriaFogazza.getId()));
        }

        @Test
        @DisplayName("deve lançar exception caso categoria não encontrada")
        void deveLancarExceptionCasoCategoriaFogazzaNaoEncontrada(){
            CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
            categoriaFogazza.setId(1);

            Fogazzas fogazzas = new Fogazzas();
            fogazzas.setNome("queijo");
            fogazzas.setCategoriaFogazza(categoriaFogazza);
            fogazzas.setId(1);

            Mockito.when(fogazzasRepository.findById(fogazzas.getId())).thenReturn(Optional.of(fogazzas));
            Mockito.when(categoriaFogazzaRepository.findById(categoriaFogazza.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> fogazzasService.atualizar(fogazzas.getId(), fogazzas, categoriaFogazza.getId()));
        }

        @Test
        @DisplayName("deve lançar exception caso fogazza não encontrada")
        void deveLancarExceptionCasoFogazzaNaoEncontrada(){
            CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
            categoriaFogazza.setId(1);

            Fogazzas fogazzas = new Fogazzas();
            fogazzas.setNome("queijo");
            fogazzas.setCategoriaFogazza(categoriaFogazza);
            fogazzas.setId(1);

            Mockito.when(fogazzasRepository.findById(fogazzas.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> fogazzasService.atualizar(fogazzas.getId(), fogazzas, categoriaFogazza.getId()));
        }
    }

    @Nested
    @DisplayName("Método deletar")
    class deletar {

        @Test
        @DisplayName("deve deletar corretamente")
        void deveDeletarCorretamente(){
            CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
            categoriaFogazza.setId(1);

            Fogazzas fogazzas = new Fogazzas();
            fogazzas.setNome("queijo");
            fogazzas.setCategoriaFogazza(categoriaFogazza);
            fogazzas.setId(1);

            Mockito.when(fogazzasRepository.existsById(fogazzas.getId())).thenReturn(true);
            fogazzasService.deletar(fogazzas.getId());

            Mockito.verify(fogazzasRepository, Mockito.times(1)).deleteById(fogazzas.getId());
        }

        @Test
        @DisplayName("deve lançar exception caso fogazza não encontrada")
        void deveLancarExceptionCasoFogazzaNaoEncontrada(){
            CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
            categoriaFogazza.setId(1);

            Fogazzas fogazzas = new Fogazzas();
            fogazzas.setNome("queijo");
            fogazzas.setCategoriaFogazza(categoriaFogazza);
            fogazzas.setId(1);

            Mockito.when(fogazzasRepository.existsById(fogazzas.getId())).thenReturn(false);

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> fogazzasService.deletar(fogazzas.getId()));
        }
    }
}