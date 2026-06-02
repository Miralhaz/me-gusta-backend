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
import school.sptech.megusta.dto.consumo_categoria.ConsumoCategoriaRequestDto;
import school.sptech.megusta.dto.consumo_categoria.ConsumoCategoriaResponseDto;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.repository.CategoriaInsumoRepository;
import school.sptech.megusta.repository.InsumoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de CategoriaInsumo")
class CategoriaInsumoServiceTest {

    @Mock
    private CategoriaInsumoRepository categoriaInsumoRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @InjectMocks
    private CategoriaInsumoService categoriaInsumoService;

    @Nested
    @DisplayName("Método listar")
    class Listar {

        @Test
        @DisplayName("Deve listar corretamente")
        void deveListarCorretamente() {
            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(1);
            categoriaInsumo.setNome("Frios");

            List<CategoriaInsumo> categoriaInsumoList = new ArrayList<>();
            categoriaInsumoList.add(categoriaInsumo);

            Mockito.when(categoriaInsumoRepository.findAll()).thenReturn(categoriaInsumoList);

            Assertions.assertEquals(categoriaInsumoList, categoriaInsumoService.listar());
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia")
        void deveRetornarListaVazia() {
            List<CategoriaInsumo> categoriaInsumoList = new ArrayList<>();

            Mockito.when(categoriaInsumoRepository.findAll()).thenReturn(categoriaInsumoList);

            Assertions.assertEquals(categoriaInsumoList, categoriaInsumoService.listar());
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findAll();
        }

    }

    @Nested
    @DisplayName("Método buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("Deve buscar corretamente")
        void deveBuscarCorretamente() {
            Integer id = 1;
            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(id);
            categoriaInsumo.setNome("Frios");

            Mockito.when(categoriaInsumoRepository.findById(id))
                    .thenReturn(Optional.of(categoriaInsumo));

            Assertions.assertEquals(categoriaInsumo,
                    categoriaInsumoService.buscarPorId(id));
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findById(id);
        }

        @Test
        @DisplayName("Deve lançar exception caso categoria não encontrada")
        void deveLancarExceptionCasoCategoriaNotFound() {
            Integer id = 1;

            Mockito.when(categoriaInsumoRepository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> categoriaInsumoService.buscarPorId(id));
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findById(id);
        }
    }

    @Nested
    @DisplayName("Método cadastrar")
    class Cadastrar {

        @Test
        @DisplayName("Deve cadastrar corretamente")
        void deveCadastrarCorretamente() {
            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setNome("  Frios  ");

            Mockito.when(categoriaInsumoRepository.existsByNomeIgnoreCase("Frios"))
                    .thenReturn(false);

            Mockito.when(categoriaInsumoRepository.save(Mockito.any(CategoriaInsumo.class)))
                    .thenReturn(categoriaInsumo);

            CategoriaInsumo resultado = categoriaInsumoService.cadastrar(categoriaInsumo);

            Assertions.assertNotNull(resultado);
            Assertions.assertEquals("Frios", resultado.getNome());
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).existsByNomeIgnoreCase("Frios");
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).save(Mockito.any(CategoriaInsumo.class));
        }

        @Test
        @DisplayName("Deve lançar exception caso categoria já exista")
        void deveLancarExceptionCasoCategoriaJaExista() {
            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setNome("Frios");

            Mockito.when(categoriaInsumoRepository.existsByNomeIgnoreCase("Frios"))
                    .thenReturn(true);

            Assertions.assertThrows(RecursoConflitoException.class,
                    () -> categoriaInsumoService.cadastrar(categoriaInsumo));
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).existsByNomeIgnoreCase("Frios");
            Mockito.verify(categoriaInsumoRepository, Mockito.never()).save(Mockito.any(CategoriaInsumo.class));
        }
    }

    @Nested
    @DisplayName("Método atualizar")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar corretamente")
        void deveAtualizarCorretamente() {
            Integer id = 1;
            CategoriaInsumo categoriaExistente = new CategoriaInsumo();
            categoriaExistente.setId(id);
            categoriaExistente.setNome("Frios");

            CategoriaInsumo categoriaAtualizada = new CategoriaInsumo();
            categoriaAtualizada.setNome("  Carnes  ");

            Mockito.when(categoriaInsumoRepository.findById(id))
                    .thenReturn(Optional.of(categoriaExistente));

            Mockito.when(categoriaInsumoRepository.existsByNomeIgnoreCaseAndIdNot("Carnes", id))
                    .thenReturn(false);

            Mockito.when(categoriaInsumoRepository.save(Mockito.any(CategoriaInsumo.class)))
                    .thenReturn(categoriaExistente);

            CategoriaInsumo resultado = categoriaInsumoService.atualizar(id, categoriaAtualizada);

            Assertions.assertNotNull(resultado);
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findById(id);
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).existsByNomeIgnoreCaseAndIdNot("Carnes", id);
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).save(Mockito.any(CategoriaInsumo.class));
        }

        @Test
        @DisplayName("Deve lançar exception caso categoria não encontrada")
        void deveLancarExceptionCasoCategoriaNotFound() {
            Integer id = 1;
            CategoriaInsumo categoriaAtualizada = new CategoriaInsumo();
            categoriaAtualizada.setNome("Carnes");

            Mockito.when(categoriaInsumoRepository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> categoriaInsumoService.atualizar(id, categoriaAtualizada));
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findById(id);
            Mockito.verify(categoriaInsumoRepository, Mockito.never()).save(Mockito.any(CategoriaInsumo.class));
        }

        @Test
        @DisplayName("Deve lançar exception caso categoria já exista")
        void deveLancarExceptionCasoCategoriaJaExista() {
            Integer id = 1;
            CategoriaInsumo categoriaExistente = new CategoriaInsumo();
            categoriaExistente.setId(id);
            categoriaExistente.setNome("Frios");

            CategoriaInsumo categoriaAtualizada = new CategoriaInsumo();
            categoriaAtualizada.setNome("Carnes");

            Mockito.when(categoriaInsumoRepository.findById(id))
                    .thenReturn(Optional.of(categoriaExistente));

            Mockito.when(categoriaInsumoRepository.existsByNomeIgnoreCaseAndIdNot("Carnes", id))
                    .thenReturn(true);

            Assertions.assertThrows(RecursoConflitoException.class,
                    () -> categoriaInsumoService.atualizar(id, categoriaAtualizada));
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findById(id);
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).existsByNomeIgnoreCaseAndIdNot("Carnes", id);
            Mockito.verify(categoriaInsumoRepository, Mockito.never()).save(Mockito.any(CategoriaInsumo.class));
        }
    }

    @Nested
    @DisplayName("Método deletar")
    class Deletar {

        @Test
        @DisplayName("Deve deletar corretamente")
        void deveDeletarCorretamente() {
            Integer id = 1;
            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(id);
            categoriaInsumo.setNome("Frios");

            Mockito.when(categoriaInsumoRepository.findById(id))
                    .thenReturn(Optional.of(categoriaInsumo));

            Mockito.when(insumoRepository.existsByCategoriaInsumo(categoriaInsumo))
                    .thenReturn(false);

            Mockito.doNothing().when(categoriaInsumoRepository).delete(categoriaInsumo);

            Assertions.assertDoesNotThrow(() -> categoriaInsumoService.deletar(id));
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findById(id);
            Mockito.verify(insumoRepository, Mockito.times(1)).existsByCategoriaInsumo(categoriaInsumo);
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).delete(categoriaInsumo);
        }

        @Test
        @DisplayName("Deve lançar exception caso categoria não encontrada")
        void deveLancarExceptionCasoCategoriaNotFound() {
            Integer id = 1;

            Mockito.when(categoriaInsumoRepository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> categoriaInsumoService.deletar(id));
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findById(id);
            Mockito.verify(insumoRepository, Mockito.never()).existsByCategoriaInsumo(Mockito.any(CategoriaInsumo.class));
            Mockito.verify(categoriaInsumoRepository, Mockito.never()).delete(Mockito.any(CategoriaInsumo.class));
        }

        @Test
        @DisplayName("Deve lançar exception quando há insumos vinculados")
        void deveLancarExceptionQuandoHaInsumosVinculados() {
            Integer id = 1;
            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(id);
            categoriaInsumo.setNome("Frios");

            Mockito.when(categoriaInsumoRepository.findById(id))
                    .thenReturn(Optional.of(categoriaInsumo));

            Mockito.when(insumoRepository.existsByCategoriaInsumo(categoriaInsumo))
                    .thenReturn(true);

            Assertions.assertThrows(RecursoConflitoException.class,
                    () -> categoriaInsumoService.deletar(id));
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findById(id);
            Mockito.verify(insumoRepository, Mockito.times(1)).existsByCategoriaInsumo(categoriaInsumo);
            Mockito.verify(categoriaInsumoRepository, Mockito.never()).delete(categoriaInsumo);
        }
    }

    @Nested
    @DisplayName("Método listarInsumosPorCategoria")
    class ListarInsumosPorCategoria {

        @Test
        @DisplayName("Deve listar insumos corretamente")
        void deveListarInsumosCorretamente() {
            Integer id = 1;
            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(id);
            categoriaInsumo.setNome("Frios");

            Insumo insumo1 = new Insumo();
            insumo1.setId(1);
            insumo1.setNome("Presunto");

            Insumo insumo2 = new Insumo();
            insumo2.setId(2);
            insumo2.setNome("Mussarela");

            List<Insumo> insumoList = List.of(insumo1, insumo2);

            Mockito.when(categoriaInsumoRepository.findById(id))
                    .thenReturn(Optional.of(categoriaInsumo));

            Mockito.when(insumoRepository.findByCategoriaInsumoId(id))
                    .thenReturn(insumoList);

            List<Insumo> resultado = categoriaInsumoService.listarInsumosPorCategoria(id);

            Assertions.assertEquals(2, resultado.size());
            Assertions.assertEquals(insumoList, resultado);
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findById(id);
            Mockito.verify(insumoRepository, Mockito.times(1)).findByCategoriaInsumoId(id);
        }

        @Test
        @DisplayName("Deve retornar lista vazia")
        void deveRetornarListaVazia() {
            Integer id = 1;
            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(id);
            categoriaInsumo.setNome("Frios");

            List<Insumo> insumoList = new ArrayList<>();

            Mockito.when(categoriaInsumoRepository.findById(id))
                    .thenReturn(Optional.of(categoriaInsumo));

            Mockito.when(insumoRepository.findByCategoriaInsumoId(id))
                    .thenReturn(insumoList);

            List<Insumo> resultado = categoriaInsumoService.listarInsumosPorCategoria(id);

            Assertions.assertTrue(resultado.isEmpty());
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findById(id);
            Mockito.verify(insumoRepository, Mockito.times(1)).findByCategoriaInsumoId(id);
        }

         @Test
         @DisplayName("Deve lançar exception caso categoria não encontrada")
         void deveLancarExceptionCasoCategoriaNotFound() {
             Integer id = 1;

             Mockito.when(categoriaInsumoRepository.findById(id))
                     .thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> categoriaInsumoService.listarInsumosPorCategoria(id));
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1)).findById(id);
            Mockito.verify(insumoRepository, Mockito.never()).findByCategoriaInsumoId(id);
        }
    }

    @Nested
    @DisplayName("Método calcularConsumoPorCategoriaNosUltimosDias")
    class CalcularConsumoPorCategoria {

        @Test
        @DisplayName("Deve calcular consumo corretamente")
        void deveCalcularConsumoCorretamente() {
            ConsumoCategoriaRequestDto request = new ConsumoCategoriaRequestDto();
            request.setNomeCategoria("Frios");
            request.setIntervalo(7);

            List<ConsumoCategoriaResponseDto> consumoList = List.of(
                    new ConsumoCategoriaResponseDto(new java.math.BigDecimal("10"), java.time.LocalDate.now())
            );

            Mockito.when(categoriaInsumoRepository.consumoPorCategoriaEspecifica(
                    Mockito.eq("Frios"), Mockito.any(java.time.LocalDateTime.class)
            )).thenReturn(consumoList);

            List<ConsumoCategoriaResponseDto> resultado = categoriaInsumoService.calcularConsumoPorCategoriaNosUltimosDias(request);

            Assertions.assertEquals(consumoList, resultado);
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1))
                    .consumoPorCategoriaEspecifica(Mockito.eq("Frios"), Mockito.any(java.time.LocalDateTime.class));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver consumo")
        void deveRetornarListaVazia() {
            ConsumoCategoriaRequestDto request = new ConsumoCategoriaRequestDto();
            request.setNomeCategoria("Frios");
            request.setIntervalo(7);

            List<ConsumoCategoriaResponseDto> consumoList = new ArrayList<>();

            Mockito.when(categoriaInsumoRepository.consumoPorCategoriaEspecifica(
                    Mockito.eq("Frios"), Mockito.any(java.time.LocalDateTime.class)
            )).thenReturn(consumoList);

            List<ConsumoCategoriaResponseDto> resultado = categoriaInsumoService.calcularConsumoPorCategoriaNosUltimosDias(request);

            Assertions.assertTrue(resultado.isEmpty());
            Mockito.verify(categoriaInsumoRepository, Mockito.times(1))
                    .consumoPorCategoriaEspecifica(Mockito.eq("Frios"), Mockito.any(java.time.LocalDateTime.class));
        }
    }
}