package school.sptech.megusta.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import school.sptech.megusta.config.CacheNames;
import school.sptech.megusta.config.LoggingCacheErrorHandler;
import school.sptech.megusta.dto.insumo.InsumoResponse;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.model.EntradaEstoque;
import school.sptech.megusta.model.Fornecedor;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.TipoStatus;
import school.sptech.megusta.model.UnidadeMedida;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.repository.CategoriaInsumoRepository;
import school.sptech.megusta.repository.EntradaEstoqueRepository;
import school.sptech.megusta.repository.FornecedorRepository;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.SaidaEstoqueRepository;
import school.sptech.megusta.repository.TipoStatusRepository;
import school.sptech.megusta.repository.UnidadeMedidaRepository;
import school.sptech.megusta.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Testes de invalidation das listas após escritas")
class ListCacheInvalidationOnWriteTest {

    private final CacheManager cacheManager =
            new ConcurrentMapCacheManager(CacheNames.INSUMO_LISTA, CacheNames.ENTRADAS_ESTOQUE_LISTA);
    private final CategoriaInsumoRepository categoriaRepository = mock(CategoriaInsumoRepository.class);
    private final UnidadeMedidaRepository unidadeRepository = mock(UnidadeMedidaRepository.class);
    private final TipoStatusService statusService = mock(TipoStatusService.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final FornecedorRepository fornecedorRepository = mock(FornecedorRepository.class);
    private final TipoStatusRepository tipoStatusRepository = mock(TipoStatusRepository.class);

    @AfterEach
    void limparTransacao() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    @Test
    @DisplayName("Criação de insumo evicta a lista: próxima leitura traz o novo estado")
    void insumoCreateRefreshesCachedList() {
        InsumoRepository insumoRepository = mock(InsumoRepository.class);
        EntradaEstoqueRepository entradaEstoqueRepository = mock(EntradaEstoqueRepository.class);
        Insumo antigo = insumo(1, "Queijo", 10.0);
        Insumo novo = insumo(2, "Novo queijo", 20.0);
        when(insumoRepository.findAll()).thenReturn(List.of(antigo), List.of(antigo, novo));
        when(entradaEstoqueRepository.findByDtValidadeGreaterThanEqualOrderByDtValidadeAsc(any()))
                .thenReturn(List.of());

        InsumoService service = insumoService(insumoRepository, entradaEstoqueRepository);
        InsumoService proxy = withCache(service);

        assertEquals(1, proxy.listarInsumosParaAlertas().size());

        when(insumoRepository.existsByNomeOrCodigoInsumo(novo.getNome(), novo.getCodigoInsumo())).thenReturn(false);
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(antigo.getCategoriaInsumo()));
        when(unidadeRepository.findById(1)).thenReturn(Optional.of(antigo.getUnidadeMedida()));
        when(statusService.calcularStatusEstoque(any(), any())).thenReturn(antigo.getTipoStatus());
        when(insumoRepository.save(novo)).thenReturn(novo);

        proxy.cadastrar(novo);

        List<InsumoResponse> depois = proxy.listarInsumosParaAlertas();
        assertEquals(2, depois.size());
        assertEquals("Novo queijo", depois.get(1).getNome());
        verify(insumoRepository, times(2)).findAll();
    }

    @Test
    @DisplayName("Falha na criação de insumo não evicta o valor em cache")
    void failedInsumoCreateKeepsCachedList() {
        InsumoRepository insumoRepository = mock(InsumoRepository.class);
        EntradaEstoqueRepository entradaEstoqueRepository = mock(EntradaEstoqueRepository.class);
        Insumo antigo = insumo(1, "Queijo", 10.0);
        when(insumoRepository.findAll()).thenReturn(List.of(antigo));
        when(entradaEstoqueRepository.findByDtValidadeGreaterThanEqualOrderByDtValidadeAsc(any()))
                .thenReturn(List.of());

        InsumoService proxy = withCache(insumoService(insumoRepository, entradaEstoqueRepository));
        List<InsumoResponse> antes = proxy.listarInsumosParaAlertas();

        when(insumoRepository.existsByNomeOrCodigoInsumo(antigo.getNome(), antigo.getCodigoInsumo()))
                .thenReturn(true);
        assertThrows(RecursoConflitoException.class, () -> proxy.cadastrar(antigo));

        assertEquals(antes, proxy.listarInsumosParaAlertas());
        verify(insumoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Criação de entrada evicta ambas as listas: próximas leituras trazem o novo estoque")
    void stockEntryCreateRefreshesBothCachedLists() {
        InsumoRepository insumoRepository = mock(InsumoRepository.class);
        EntradaEstoqueRepository entradaEstoqueRepository = mock(EntradaEstoqueRepository.class);
        Insumo insumoAntes = insumo(1, "Queijo", 10.0);
        Insumo insumoDepois = insumo(1, "Queijo", 12.0);
        EntradaEstoque entrada = entrada(insumoAntes);

        when(insumoRepository.findAll()).thenReturn(List.of(insumoAntes), List.of(insumoDepois));
        when(entradaEstoqueRepository.findAll()).thenReturn(List.of(), List.of(entrada));
        when(entradaEstoqueRepository.findByDtValidadeGreaterThanEqualOrderByDtValidadeAsc(any()))
                .thenReturn(List.of());

        InsumoService insumos = withCache(insumoService(insumoRepository, entradaEstoqueRepository));
        EntradaEstoqueService entradas = withCache(entradaEstoqueService(entradaEstoqueRepository, insumoRepository));

        assertEquals(10.0, insumos.listarInsumosParaAlertas().get(0).getQuantidadeAtual());
        assertEquals(0, entradas.listarEntradasParaAlertas().size());

        when(insumoRepository.findById(1)).thenReturn(Optional.of(insumoAntes));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(entrada.getUsuario()));
        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(entrada.getFornecedor()));
        when(tipoStatusRepository.findById(1)).thenReturn(Optional.of(insumoAntes.getTipoStatus()));
        when(unidadeRepository.findById(1)).thenReturn(Optional.of(insumoAntes.getUnidadeMedida()));
        when(statusService.calcularStatusEstoque(any(), any())).thenReturn(insumoAntes.getTipoStatus());
        when(entradaEstoqueRepository.save(entrada)).thenReturn(entrada);

        entradas.cadastrar(entrada);

        assertEquals(12.0, insumos.listarInsumosParaAlertas().get(0).getQuantidadeAtual());
        assertEquals(1, entradas.listarEntradasParaAlertas().size());
        verify(insumoRepository, times(2)).findAll();
        verify(entradaEstoqueRepository, times(2)).findAll();
    }

    @Test
    @DisplayName("Rollback de escrita transacional não evicta: cache permanece com o valor original")
    void rolledBackWriteKeepsOriginalCachedValue() {
        InsumoRepository insumoRepository = mock(InsumoRepository.class);
        EntradaEstoqueRepository entradaEstoqueRepository = mock(EntradaEstoqueRepository.class);
        Insumo antigo = insumo(1, "Queijo", 10.0);
        Insumo novo = insumo(2, "Novo queijo", 20.0);
        when(insumoRepository.findAll()).thenReturn(List.of(antigo));
        when(entradaEstoqueRepository.findByDtValidadeGreaterThanEqualOrderByDtValidadeAsc(any()))
                .thenReturn(List.of());

        InsumoService proxy = withCache(insumoService(insumoRepository, entradaEstoqueRepository));
        List<InsumoResponse> antes = proxy.listarInsumosParaAlertas();

        when(insumoRepository.existsByNomeOrCodigoInsumo(novo.getNome(), novo.getCodigoInsumo())).thenReturn(false);
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(antigo.getCategoriaInsumo()));
        when(unidadeRepository.findById(1)).thenReturn(Optional.of(antigo.getUnidadeMedida()));
        when(statusService.calcularStatusEstoque(any(), any())).thenReturn(antigo.getTipoStatus());
        when(insumoRepository.save(novo)).thenReturn(novo);

        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);
        proxy.cadastrar(novo);
        // Simula rollback: descarta as sincronizações sem disparar afterCommit
        TransactionSynchronizationManager.clearSynchronization();

        assertEquals(antes, proxy.listarInsumosParaAlertas());
        verify(insumoRepository, times(1)).findAll();
    }

    private InsumoService insumoService(InsumoRepository insumoRepository,
                                        EntradaEstoqueRepository entradaEstoqueRepository) {
        return new InsumoService(insumoRepository, categoriaRepository, unidadeRepository, statusService,
                entradaEstoqueRepository, mock(SaidaEstoqueRepository.class),
                new StockListCacheInvalidator(cacheManager));
    }

    private EntradaEstoqueService entradaEstoqueService(EntradaEstoqueRepository entradaEstoqueRepository,
                                                        InsumoRepository insumoRepository) {
        return new EntradaEstoqueService(entradaEstoqueRepository, insumoRepository, usuarioRepository,
                fornecedorRepository, tipoStatusRepository, unidadeRepository, statusService,
                new StockListCacheInvalidator(cacheManager));
    }

    @SuppressWarnings("unchecked")
    private <T> T withCache(T target) {
        CacheInterceptor interceptor = new CacheInterceptor();
        interceptor.setCacheOperationSource(new AnnotationCacheOperationSource());
        interceptor.setCacheManager(cacheManager);
        interceptor.setErrorHandler(new LoggingCacheErrorHandler());
        interceptor.afterSingletonsInstantiated();
        ProxyFactory factory = new ProxyFactory(target);
        factory.addAdvice(interceptor);
        return (T) factory.getProxy();
    }

    private Insumo insumo(Integer id, String nome, Double quantidade) {
        CategoriaInsumo categoria = new CategoriaInsumo();
        categoria.setId(1);
        categoria.setNome("Laticínios");

        UnidadeMedida unidade = new UnidadeMedida();
        unidade.setId(1);
        unidade.setUnidade("kg");

        TipoStatus status = new TipoStatus();
        status.setId(1);
        status.setNome("OK");

        Insumo insumo = new Insumo();
        insumo.setId(id);
        insumo.setNome(nome);
        insumo.setCodigoInsumo("IN-" + id);
        insumo.setEstoqueMinimo(1.0);
        insumo.setQtdAtual(quantidade);
        insumo.setAtivo(true);
        insumo.setDtCadastro(LocalDateTime.of(2026, 11, 1, 10, 0));
        insumo.setCategoriaInsumo(categoria);
        insumo.setUnidadeMedida(unidade);
        insumo.setTipoStatus(status);
        return insumo;
    }

    private EntradaEstoque entrada(Insumo insumo) {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(1);
        fornecedor.setNome("Fornecedor");
        fornecedor.setCnpj("12345678000190");

        EntradaEstoque entrada = new EntradaEstoque();
        entrada.setId(1);
        entrada.setInsumo(insumo);
        entrada.setUsuario(usuario);
        entrada.setFornecedor(fornecedor);
        entrada.setTipoStatus(insumo.getTipoStatus());
        entrada.setUnidadeMedida(insumo.getUnidadeMedida());
        entrada.setQuantidadeAbsoluta(new BigDecimal("2.00"));
        entrada.setQuantidadeRelativa(new BigDecimal("1.00"));
        return entrada;
    }
}
