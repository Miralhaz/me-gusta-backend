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
import school.sptech.megusta.repository.CategoriaFogazzaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da CategoriaFogazzaService")
class CategoriaFogazzaServiceTest {

    @Mock
    private CategoriaFogazzaRepository categoriaFogazzaRepository;

    @InjectMocks
    private CategoriaFogazzaService categoriaFogazzaService;

    @Nested
    @DisplayName("Método - Listar")
    class Listar{

        @Test
        @DisplayName("Deve retornar uma lista de categorias de fogazza")
        void retornarLista(){

            CategoriaFogazza cF1 = new CategoriaFogazza();
            cF1.setId(1);
            cF1.setNome("Embutidos");

            CategoriaFogazza cF2 = new CategoriaFogazza();
            cF2.setId(2);
            cF2.setNome("Carnes");

            Mockito.when(categoriaFogazzaRepository.findAll()).thenReturn(List.of(cF1, cF2));

            List<CategoriaFogazza> categoriasFogazza = categoriaFogazzaService.listar();

            Assertions.assertEquals(2, categoriasFogazza.size());
            Mockito.verify(categoriaFogazzaRepository, Mockito.times(1)).findAll();

        }

        @Test
        @DisplayName("Deve retornar uma lista vazia")
        void retornarListaVazia(){

            List<CategoriaFogazza> categoriasFogazza = new ArrayList<>();

            Mockito.when(categoriaFogazzaRepository.findAll()).thenReturn(categoriasFogazza);

            Assertions.assertEquals(categoriasFogazza, categoriaFogazzaService.listar());
            Mockito.verify(categoriaFogazzaRepository, Mockito.times(1)).findAll();

        }

    }

    @Nested
    @DisplayName("Método - BuscarPorId")
    class BuscarPorId{

        @Test
        @DisplayName("Deve retornar uma busca por ID de categoria de fogazza")
        void retornarBuscaDeCategoriaDeFogazzasPorId(){

            CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
            categoriaFogazza.setId(1);
            categoriaFogazza.setNome("Embutidos");

            Mockito.when(categoriaFogazzaRepository.findById(categoriaFogazza.getId())).thenReturn(Optional.of(categoriaFogazza));

            Assertions.assertEquals(categoriaFogazza, categoriaFogazzaService.buscarPorId(categoriaFogazza.getId()));
            Mockito.verify(categoriaFogazzaRepository, Mockito.times(1)).findById(categoriaFogazza.getId());

        }

        @Test
        @DisplayName("Deve lançar Exception quando ID for inexistente")
        void lancarExceptionPorIdInexistente(){

            Optional<CategoriaFogazza> categoriaFogazzaInexistente = Optional.empty();

            Mockito.when(categoriaFogazzaRepository.findById(70)).thenReturn(categoriaFogazzaInexistente);

            Assertions.assertThrows(
                    RecursoNaoEncontradoException.class,
                    () -> categoriaFogazzaService.buscarPorId(70)
            );

        }

    }

    @Nested
    @DisplayName("Método - Cadastrar")
    class Cadastrar{

        @Test
        @DisplayName("Deve cadastrar uma categoria de fogazza corretamente")
        void cadastrarCategoriaFogazza() {

            CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
            categoriaFogazza.setId(1);
            categoriaFogazza.setNome("Embutidos");

            Mockito.when(categoriaFogazzaRepository.existsByNome(categoriaFogazza.getNome())).thenReturn(false);
            Mockito.when(categoriaFogazzaRepository.save(categoriaFogazza)).thenReturn(categoriaFogazza);

            CategoriaFogazza categoriaFogazzaAdd = categoriaFogazzaService.cadastrar(categoriaFogazza);

            assertNotNull(categoriaFogazzaAdd);
            Assertions.assertEquals(categoriaFogazza, categoriaFogazzaAdd);
            Mockito.verify(categoriaFogazzaRepository, Mockito.times(1)).save(categoriaFogazza);

        }

        @Test
        @DisplayName("Deve lançar Exception quando categoria de fogazza já for existente")
        void lancarExceptionQuandoCategoriaJaExistente(){

            CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
            categoriaFogazza.setId(1);
            categoriaFogazza.setNome("Embutidos");

            Mockito.when(categoriaFogazzaRepository.existsByNome(categoriaFogazza.getNome())).thenReturn(true);

            Assertions.assertThrows(
                    RecursoConflitoException.class,
                    () -> categoriaFogazzaService.cadastrar(categoriaFogazza)
            );
            Mockito.verify(categoriaFogazzaRepository, Mockito.never()).save(categoriaFogazza);

        }

    }

    @Nested
    @DisplayName("Método - Deletar")
    class Deletar{

        @Test
        @DisplayName("Deve deletar por ID uma categoria de fogazza corretamente")
        void deletarCategoriaFogazza() {

            CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
            categoriaFogazza.setId(1);
            categoriaFogazza.setNome("Embutidos");

            Mockito.when(categoriaFogazzaRepository.existsById(categoriaFogazza.getId())).thenReturn(true);
            Mockito.doNothing().when(categoriaFogazzaRepository).deleteById(categoriaFogazza.getId());

            Assertions.assertDoesNotThrow(() -> categoriaFogazzaService.deletar(categoriaFogazza.getId()));
            Mockito.verify(categoriaFogazzaRepository, Mockito.times(1)).deleteById(categoriaFogazza.getId());

        }

        @Test
        @DisplayName("Deve lançar Exception quando ID for inexistente")
        void lancarExecptionPorIdInexistente(){

            Mockito.when(categoriaFogazzaRepository.existsById(70)).thenReturn(false);

            Assertions.assertThrows(
                    RecursoNaoEncontradoException.class,
                    () -> categoriaFogazzaService.deletar(70)
            );
            Mockito.verify(categoriaFogazzaRepository, Mockito.never()).deleteById(70);

        }

    }

}