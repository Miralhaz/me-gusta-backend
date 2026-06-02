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
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.*;
import school.sptech.megusta.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe EntradaEstoqueService")
class EntradaEstoqueServiceTest {

    @Mock
    private EntradaEstoqueRepository entradaEstoqueRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private TipoStatusRepository tipoStatusRepository;

    @Mock
    private UnidadeMedidaRepository unidadeMedidaRepository;

    @InjectMocks
    private EntradaEstoqueService entradaEstoqueService;

    @Nested
    @DisplayName("Método listar")
    class listar {

        @Test
        @DisplayName("deve listar corretamente")
        void deveListarCorretamente(){
            EntradaEstoque entrada = new EntradaEstoque();
            List<EntradaEstoque> entradas = new ArrayList<>();
            entradas.add(entrada);

            Mockito.when(entradaEstoqueRepository.findAll()).thenReturn(entradas);

            Assertions.assertEquals(entradas, entradaEstoqueService.listar());
        }

        @Test
        @DisplayName("deve retornar lista vazia")
        void deveRetornarListaVazia(){
            List<EntradaEstoque> entradas = new ArrayList<>();

            Mockito.when(entradaEstoqueRepository.findAll()).thenReturn(entradas);

            Assertions.assertEquals(entradas, entradaEstoqueService.listar());
        }
    }

    @Nested
    @DisplayName("Método buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("deve buscar corretamente")
        void deveBuscarCorretamente(){
            EntradaEstoque entrada = new EntradaEstoque();
            entrada.setId(1);

            Mockito.when(entradaEstoqueRepository.findById(entrada.getId())).thenReturn(Optional.of(entrada));

            Assertions.assertEquals(entrada, entradaEstoqueService.buscarPorId(entrada.getId()));
        }

        @Test
        @DisplayName("deve lançar exception caso não encontrada")
        void deveLancarExcecaoCasoNaoEncontrada(){
            Mockito.when(entradaEstoqueRepository.findById(1)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.buscarPorId(1));
        }
    }

    @Nested
    @DisplayName("Método cadastrar")
    class cadastrar {

        @Test
        @DisplayName("deve cadastrar corretamente")
        void deveCadastrarCorretamente(){
            EntradaEstoque entrada = new EntradaEstoque();

            Insumo insumo = new Insumo(); insumo.setId(1);
            Usuario usuario = new Usuario(); usuario.setId(1);
            Fornecedor fornecedor = new Fornecedor(); fornecedor.setId(1);
            TipoStatus tipoStatus = new TipoStatus(); tipoStatus.setId(1);
            UnidadeMedida unidadeMedida = new UnidadeMedida(); unidadeMedida.setId(1);

            entrada.setInsumo(insumo);
            entrada.setUsuario(usuario);
            entrada.setFornecedor(fornecedor);
            entrada.setTipoStatus(tipoStatus);
            entrada.setUnidadeMedida(unidadeMedida);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
            Mockito.when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            Mockito.when(tipoStatusRepository.findById(tipoStatus.getId())).thenReturn(Optional.of(tipoStatus));
            Mockito.when(unidadeMedidaRepository.findById(unidadeMedida.getId())).thenReturn(Optional.of(unidadeMedida));
            Mockito.when(entradaEstoqueRepository.save(entrada)).thenReturn(entrada);

            Assertions.assertEquals(entrada, entradaEstoqueService.cadastrar(entrada));
        }

        @Test
        @DisplayName("deve lançar exception quando insumo não encontrado")
        void deveLancarQuandoInsumoNaoEncontrado(){
            EntradaEstoque entrada = new EntradaEstoque();
            Insumo insumo = new Insumo(); insumo.setId(1);
            entrada.setInsumo(insumo);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.cadastrar(entrada));
        }

        @Test
        @DisplayName("deve lançar exception quando usuario não encontrado")
        void deveLancarQuandoUsuarioNaoEncontrado(){
            EntradaEstoque entrada = new EntradaEstoque();
            Insumo insumo = new Insumo(); insumo.setId(1);
            Usuario usuario = new Usuario(); usuario.setId(1);

            entrada.setInsumo(insumo);
            entrada.setUsuario(usuario);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.cadastrar(entrada));
        }

        @Test
        @DisplayName("deve lançar exception quando fornecedor não encontrado")
        void deveLancarQuandoFornecedorNaoEncontrado(){
            EntradaEstoque entrada = new EntradaEstoque();
            Insumo insumo = new Insumo(); insumo.setId(1);
            Usuario usuario = new Usuario(); usuario.setId(1);
            Fornecedor fornecedor = new Fornecedor(); fornecedor.setId(1);

            entrada.setInsumo(insumo);
            entrada.setUsuario(usuario);
            entrada.setFornecedor(fornecedor);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
            Mockito.when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.cadastrar(entrada));
        }

        @Test
        @DisplayName("deve lançar exception quando tipoStatus não encontrado")
        void deveLancarQuandoTipoStatusNaoEncontrado(){
            EntradaEstoque entrada = new EntradaEstoque();
            Insumo insumo = new Insumo(); insumo.setId(1);
            Usuario usuario = new Usuario(); usuario.setId(1);
            Fornecedor fornecedor = new Fornecedor(); fornecedor.setId(1);
            TipoStatus tipoStatus = new TipoStatus(); tipoStatus.setId(1);

            entrada.setInsumo(insumo);
            entrada.setUsuario(usuario);
            entrada.setFornecedor(fornecedor);
            entrada.setTipoStatus(tipoStatus);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
            Mockito.when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            Mockito.when(tipoStatusRepository.findById(tipoStatus.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.cadastrar(entrada));
        }

        @Test
        @DisplayName("deve lançar exception quando unidadeMedida não encontrada")
        void deveLancarQuandoUnidadeMedidaNaoEncontrado(){
            EntradaEstoque entrada = new EntradaEstoque();
            Insumo insumo = new Insumo(); insumo.setId(1);
            Usuario usuario = new Usuario(); usuario.setId(1);
            Fornecedor fornecedor = new Fornecedor(); fornecedor.setId(1);
            TipoStatus tipoStatus = new TipoStatus(); tipoStatus.setId(1);
            UnidadeMedida unidadeMedida = new UnidadeMedida(); unidadeMedida.setId(1);

            entrada.setInsumo(insumo);
            entrada.setUsuario(usuario);
            entrada.setFornecedor(fornecedor);
            entrada.setTipoStatus(tipoStatus);
            entrada.setUnidadeMedida(unidadeMedida);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
            Mockito.when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            Mockito.when(tipoStatusRepository.findById(tipoStatus.getId())).thenReturn(Optional.of(tipoStatus));
            Mockito.when(unidadeMedidaRepository.findById(unidadeMedida.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.cadastrar(entrada));
        }
    }

    @Nested
    @DisplayName("Método atualizar")
    class atualizar {

        @Test
        @DisplayName("deve atualizar corretamente")
        void deveAtualizarCorretamente(){
            EntradaEstoque entrada = new EntradaEstoque(); entrada.setId(1);

            Insumo insumo = new Insumo(); insumo.setId(1);
            Usuario usuario = new Usuario(); usuario.setId(1);
            Fornecedor fornecedor = new Fornecedor(); fornecedor.setId(1);
            TipoStatus tipoStatus = new TipoStatus(); tipoStatus.setId(1);
            UnidadeMedida unidadeMedida = new UnidadeMedida(); unidadeMedida.setId(1);

            entrada.setInsumo(insumo);
            entrada.setUsuario(usuario);
            entrada.setFornecedor(fornecedor);
            entrada.setTipoStatus(tipoStatus);
            entrada.setUnidadeMedida(unidadeMedida);
            entrada.setQuantidadeAbsoluta(BigDecimal.valueOf(10.0));
            entrada.setQuantidadeRelativa(BigDecimal.valueOf(1.0));
            entrada.setLote("L1");
            entrada.setDtValidade(LocalDate.now());
            entrada.setDtPedido(LocalDate.now());
            entrada.setVlTotal(BigDecimal.valueOf(50.0));

            EntradaEstoque existente = new EntradaEstoque(); existente.setId(1);

            Mockito.when(entradaEstoqueRepository.findById(entrada.getId())).thenReturn(Optional.of(existente));
            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
            Mockito.when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            Mockito.when(tipoStatusRepository.findById(tipoStatus.getId())).thenReturn(Optional.of(tipoStatus));
            Mockito.when(unidadeMedidaRepository.findById(unidadeMedida.getId())).thenReturn(Optional.of(unidadeMedida));
            Mockito.when(entradaEstoqueRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

            EntradaEstoque atualizada = entradaEstoqueService.atualizar(entrada, entrada.getId());

            Assertions.assertEquals(existente.getId(), atualizada.getId());
            Assertions.assertEquals(entrada.getQuantidadeAbsoluta(), atualizada.getQuantidadeAbsoluta());
            Assertions.assertEquals(entrada.getLote(), atualizada.getLote());
        }

        @Test
        @DisplayName("deve lançar exception quando entrada não existe")
        void deveLancarQuandoEntradaNaoExiste(){
            EntradaEstoque entrada = new EntradaEstoque(); entrada.setId(1);

            Mockito.when(entradaEstoqueRepository.findById(entrada.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.atualizar(entrada, entrada.getId()));
        }

        @Test
        @DisplayName("deve lançar exception quando insumo não encontrado durante atualização")
        void deveLancarQuandoInsumoNaoEncontradoNaAtualizacao(){
            EntradaEstoque entrada = new EntradaEstoque(); entrada.setId(1);
            Insumo insumo = new Insumo(); insumo.setId(1);
            entrada.setInsumo(insumo);

            EntradaEstoque existente = new EntradaEstoque(); existente.setId(1);

            Mockito.when(entradaEstoqueRepository.findById(entrada.getId())).thenReturn(Optional.of(existente));
            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.atualizar(entrada, entrada.getId()));
        }

        @Test
        @DisplayName("deve lançar exception quando usuario não encontrado durante atualização")
        void deveLancarQuandoUsuarioNaoEncontradoNaAtualizacao(){
            EntradaEstoque entrada = new EntradaEstoque(); entrada.setId(1);
            Insumo insumo = new Insumo(); insumo.setId(1);
            Usuario usuario = new Usuario(); usuario.setId(1);

            entrada.setInsumo(insumo);
            entrada.setUsuario(usuario);

            EntradaEstoque existente = new EntradaEstoque(); existente.setId(1);

            Mockito.when(entradaEstoqueRepository.findById(entrada.getId())).thenReturn(Optional.of(existente));
            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.atualizar(entrada, entrada.getId()));
        }

        @Test
        @DisplayName("deve lançar exception quando fornecedor não encontrado durante atualização")
        void deveLancarQuandoFornecedorNaoEncontradoNaAtualizacao(){
            EntradaEstoque entrada = new EntradaEstoque(); entrada.setId(1);
            Insumo insumo = new Insumo(); insumo.setId(1);
            Usuario usuario = new Usuario(); usuario.setId(1);
            Fornecedor fornecedor = new Fornecedor(); fornecedor.setId(1);

            entrada.setInsumo(insumo);
            entrada.setUsuario(usuario);
            entrada.setFornecedor(fornecedor);

            EntradaEstoque existente = new EntradaEstoque(); existente.setId(1);

            Mockito.when(entradaEstoqueRepository.findById(entrada.getId())).thenReturn(Optional.of(existente));
            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
            Mockito.when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.atualizar(entrada, entrada.getId()));
        }

        @Test
        @DisplayName("deve lançar exception quando tipoStatus não encontrado durante atualização")
        void deveLancarQuandoTipoStatusNaoEncontradoNaAtualizacao(){
            EntradaEstoque entrada = new EntradaEstoque(); entrada.setId(1);
            Insumo insumo = new Insumo(); insumo.setId(1);
            Usuario usuario = new Usuario(); usuario.setId(1);
            Fornecedor fornecedor = new Fornecedor(); fornecedor.setId(1);
            TipoStatus tipoStatus = new TipoStatus(); tipoStatus.setId(1);

            entrada.setInsumo(insumo);
            entrada.setUsuario(usuario);
            entrada.setFornecedor(fornecedor);
            entrada.setTipoStatus(tipoStatus);

            EntradaEstoque existente = new EntradaEstoque(); existente.setId(1);

            Mockito.when(entradaEstoqueRepository.findById(entrada.getId())).thenReturn(Optional.of(existente));
            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
            Mockito.when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            Mockito.when(tipoStatusRepository.findById(tipoStatus.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.atualizar(entrada, entrada.getId()));
        }

        @Test
        @DisplayName("deve lançar exception quando unidadeMedida não encontrada durante atualização")
        void deveLancarQuandoUnidadeMedidaNaoEncontradaNaAtualizacao(){
            EntradaEstoque entrada = new EntradaEstoque(); entrada.setId(1);
            Insumo insumo = new Insumo(); insumo.setId(1);
            Usuario usuario = new Usuario(); usuario.setId(1);
            Fornecedor fornecedor = new Fornecedor(); fornecedor.setId(1);
            TipoStatus tipoStatus = new TipoStatus(); tipoStatus.setId(1);
            UnidadeMedida unidadeMedida = new UnidadeMedida(); unidadeMedida.setId(1);

            entrada.setInsumo(insumo);
            entrada.setUsuario(usuario);
            entrada.setFornecedor(fornecedor);
            entrada.setTipoStatus(tipoStatus);
            entrada.setUnidadeMedida(unidadeMedida);

            EntradaEstoque existente = new EntradaEstoque(); existente.setId(1);

            Mockito.when(entradaEstoqueRepository.findById(entrada.getId())).thenReturn(Optional.of(existente));
            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
            Mockito.when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            Mockito.when(tipoStatusRepository.findById(tipoStatus.getId())).thenReturn(Optional.of(tipoStatus));
            Mockito.when(unidadeMedidaRepository.findById(unidadeMedida.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.atualizar(entrada, entrada.getId()));
        }
    }

    @Nested
    @DisplayName("Método deletar")
    class deletar {

        @Test
        @DisplayName("deve deletar corretamente")
        void deveDeletarCorretamente(){
            EntradaEstoque entrada = new EntradaEstoque(); entrada.setId(1);

            Mockito.when(entradaEstoqueRepository.findById(entrada.getId())).thenReturn(Optional.of(entrada));

            entradaEstoqueService.deletar(entrada.getId());

            Mockito.verify(entradaEstoqueRepository).delete(entrada);
        }

        @Test
        @DisplayName("deve lançar exception quando entrada não existe")
        void deveLancarQuandoNaoExiste(){
            Mockito.when(entradaEstoqueRepository.findById(1)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.deletar(1));
        }
    }

    @Nested
    @DisplayName("Métodos de busca específicas")
    class buscas {

        @Test
        @DisplayName("deve buscar por insumo quando existir")
        void deveBuscarPorInsumoQuandoExistir(){
            Insumo insumo = new Insumo(); insumo.setId(1);
            List<EntradaEstoque> entradas = new ArrayList<>(); entradas.add(new EntradaEstoque());

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(entradaEstoqueRepository.findByInsumoId(insumo.getId())).thenReturn(entradas);

            Assertions.assertEquals(entradas, entradaEstoqueService.buscarPorInsumo(insumo.getId()));
        }

        @Test
        @DisplayName("deve retornar lista vazia ao buscar por insumo sem entradas")
        void deveRetornarListaVaziaAoBuscarPorInsumoSemEntradas(){
            Insumo insumo = new Insumo(); insumo.setId(1);
            List<EntradaEstoque> entradas = new ArrayList<>();

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(entradaEstoqueRepository.findByInsumoId(insumo.getId())).thenReturn(entradas);

            Assertions.assertEquals(entradas, entradaEstoqueService.buscarPorInsumo(insumo.getId()));
        }

        @Test
        @DisplayName("deve lançar exception ao buscar por insumo inexistente")
        void deveLancarAoBuscarPorInsumoInexistente(){
            Mockito.when(insumoRepository.findById(1)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.buscarPorInsumo(1));
        }

        @Test
        @DisplayName("deve buscar por fornecedor quando existir")
        void deveBuscarPorFornecedorQuandoExistir(){
            Fornecedor fornecedor = new Fornecedor(); fornecedor.setId(1);
            List<EntradaEstoque> entradas = new ArrayList<>(); entradas.add(new EntradaEstoque());

            Mockito.when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            Mockito.when(entradaEstoqueRepository.findByFornecedorId(fornecedor.getId())).thenReturn(entradas);

            Assertions.assertEquals(entradas, entradaEstoqueService.buscarPorFornecedor(fornecedor.getId()));
        }

        @Test
        @DisplayName("deve retornar lista vazia ao buscar por fornecedor sem entradas")
        void deveRetornarListaVaziaAoBuscarPorFornecedorSemEntradas(){
            Fornecedor fornecedor = new Fornecedor(); fornecedor.setId(1);
            List<EntradaEstoque> entradas = new ArrayList<>();

            Mockito.when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            Mockito.when(entradaEstoqueRepository.findByFornecedorId(fornecedor.getId())).thenReturn(entradas);

            Assertions.assertEquals(entradas, entradaEstoqueService.buscarPorFornecedor(fornecedor.getId()));
        }

        @Test
        @DisplayName("deve lançar exception ao buscar por fornecedor inexistente")
        void deveLancarAoBuscarPorFornecedorInexistente(){
            Mockito.when(fornecedorRepository.findById(1)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> entradaEstoqueService.buscarPorFornecedor(1));
        }

        @Test
        @DisplayName("deve buscar por data pedido corretamente")
        void deveBuscarPorDataPedido(){
            LocalDate inicio = LocalDate.now().minusDays(5);
            LocalDate fim = LocalDate.now();
            List<EntradaEstoque> entradas = new ArrayList<>(); entradas.add(new EntradaEstoque());

            Mockito.when(entradaEstoqueRepository.findByDtEntradaBetween(inicio, fim)).thenReturn(entradas);

            Assertions.assertEquals(entradas, entradaEstoqueService.buscarPorDataPedido(inicio, fim));
        }

        @Test
        @DisplayName("deve retornar lista vazia ao buscar por data pedido sem entradas")
        void deveRetornarListaVaziaAoBuscarPorDataPedidoSemEntradas(){
            LocalDate inicio = LocalDate.now().minusDays(5);
            LocalDate fim = LocalDate.now();
            List<EntradaEstoque> entradas = new ArrayList<>();

            Mockito.when(entradaEstoqueRepository.findByDtEntradaBetween(inicio, fim)).thenReturn(entradas);

            Assertions.assertEquals(entradas, entradaEstoqueService.buscarPorDataPedido(inicio, fim));
        }

        @Test
        @DisplayName("deve retornar múltiplas entradas ao buscar por data pedido")
        void deveRetornarMultiplasEntradasAoBuscarPorDataPedido(){
            LocalDate inicio = LocalDate.now().minusDays(10);
            LocalDate fim = LocalDate.now();
            List<EntradaEstoque> entradas = new ArrayList<>();
            entradas.add(new EntradaEstoque());
            entradas.add(new EntradaEstoque());
            entradas.add(new EntradaEstoque());

            Mockito.when(entradaEstoqueRepository.findByDtEntradaBetween(inicio, fim)).thenReturn(entradas);

            List<EntradaEstoque> resultado = entradaEstoqueService.buscarPorDataPedido(inicio, fim);

            Assertions.assertEquals(3, resultado.size());
            Assertions.assertEquals(entradas, resultado);
        }
    }

}