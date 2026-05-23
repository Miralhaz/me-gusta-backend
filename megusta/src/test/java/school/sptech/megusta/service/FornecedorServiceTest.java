package school.sptech.megusta.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.Fornecedor;
import school.sptech.megusta.repository.FornecedorRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Fornecedor Service")
public class FornecedorServiceTest {

    @Mock
    private FornecedorRepository fornecedorRepository;

    @InjectMocks
    private FornecedorService fornecedorService;

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("Deve retornar lista de fornecedores")
        void deveRetornarLista() {
            Fornecedor f1 = new Fornecedor();
            f1.setId(1);
            f1.setNome("Fornecedor A");

            Fornecedor f2 = new Fornecedor();
            f2.setId(2);
            f2.setNome("Fornecedor B");

            when(fornecedorRepository.findAll()).thenReturn(List.of(f1, f2));

            List<Fornecedor> resultado = fornecedorService.listar();

            assertEquals(2, resultado.size());
            verify(fornecedorRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("cadastrar()")
    class Cadastrar {

        @Test
        @DisplayName("Deve cadastrar fornecedor com sucesso")
        void deveSalvarQuandoNaoExiste() {
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setNome("Fornecedor A");
            fornecedor.setCnpj("12345678000100");

            when(fornecedorRepository.existsByNomeAndCnpj("Fornecedor A", "12345678000100")).thenReturn(false);
            when(fornecedorRepository.save(fornecedor)).thenReturn(fornecedor);

            Fornecedor resultado = fornecedorService.cadastrar(fornecedor);

            assertNotNull(resultado);
            assertEquals("Fornecedor A", resultado.getNome());
            verify(fornecedorRepository, times(1)).save(fornecedor);
        }

        @Test
        @DisplayName("Deve lançar exceção ao cadastrar fornecedor já existente")
        void deveLancarExcecaoQuandoJaExiste() {
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setNome("Fornecedor A");
            fornecedor.setCnpj("12345678000100");

            when(fornecedorRepository.existsByNomeAndCnpj("Fornecedor A", "12345678000100")).thenReturn(true);

            assertThrows(RecursoConflitoException.class, () -> fornecedorService.cadastrar(fornecedor));
            verify(fornecedorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar fornecedor ao buscar por ID existente")
        void deveRetornarFornecedorQuandoEncontrado() {
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setId(1);
            fornecedor.setNome("Fornecedor A");

            when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));

            Fornecedor resultado = fornecedorService.buscarPorId(1);

            assertNotNull(resultado);
            assertEquals(1, resultado.getId());
            verify(fornecedorRepository, times(1)).findById(1);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar por ID inexistente")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(fornecedorRepository.findById(99)).thenReturn(Optional.empty());

            assertThrows(RecursoNaoEncontradoException.class, () -> fornecedorService.buscarPorId(99));
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar fornecedor com sucesso")
        void deveAtualizarQuandoValido() {
            Fornecedor fornecedorExistente = new Fornecedor();
            fornecedorExistente.setId(1);
            fornecedorExistente.setNome("Fornecedor Antigo");
            fornecedorExistente.setCnpj("12345678000100");

            Fornecedor fornecedorAtualizado = new Fornecedor();
            fornecedorAtualizado.setNome("Fornecedor Novo");
            fornecedorAtualizado.setCnpj("12345678000100");

            when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedorExistente));
            when(fornecedorRepository.existsByNomeAndCnpjAndIdNot("Fornecedor Novo", "12345678000100", 1)).thenReturn(false);
            when(fornecedorRepository.save(fornecedorAtualizado)).thenReturn(fornecedorAtualizado);

            Fornecedor resultado = fornecedorService.atualizar(1, fornecedorAtualizado);

            assertNotNull(resultado);
            assertEquals("Fornecedor Novo", resultado.getNome());
            assertEquals(1, fornecedorAtualizado.getId());
            verify(fornecedorRepository, times(1)).save(fornecedorAtualizado);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar fornecedor com ID inexistente")
        void deveLancarExcecaoQuandoIdNaoEncontrado() {
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setNome("Fornecedor X");
            fornecedor.setCnpj("12345678000100");

            when(fornecedorRepository.findById(99)).thenReturn(Optional.empty());

            assertThrows(RecursoNaoEncontradoException.class, () -> fornecedorService.atualizar(99, fornecedor));
            verify(fornecedorRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar com nome e CNPJ já existentes em outro fornecedor")
        void deveLancarExcecaoQuandoConflitoDeNomeECnpj() {
            Fornecedor fornecedorExistente = new Fornecedor();
            fornecedorExistente.setId(1);

            Fornecedor fornecedorAtualizado = new Fornecedor();
            fornecedorAtualizado.setNome("Fornecedor Duplicado");
            fornecedorAtualizado.setCnpj("99999999000100");

            when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedorExistente));
            when(fornecedorRepository.existsByNomeAndCnpjAndIdNot("Fornecedor Duplicado", "99999999000100", 1)).thenReturn(true);

            assertThrows(RecursoConflitoException.class, () -> fornecedorService.atualizar(1, fornecedorAtualizado));
            verify(fornecedorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("excluir()")
    class Excluir {

        @Test
        @DisplayName("Deve excluir fornecedor com sucesso")
        void deveExcluirQuandoEncontrado() {
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setId(1);

            when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));
            doNothing().when(fornecedorRepository).deleteById(1);

            assertDoesNotThrow(() -> fornecedorService.excluir(1));
            verify(fornecedorRepository, times(1)).deleteById(1);
        }

        @Test
        @DisplayName("Deve lançar exceção ao excluir fornecedor com ID inexistente")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(fornecedorRepository.findById(99)).thenReturn(Optional.empty());

            assertThrows(RecursoNaoEncontradoException.class, () -> fornecedorService.excluir(99));
            verify(fornecedorRepository, never()).deleteById(any());
        }
    }
}