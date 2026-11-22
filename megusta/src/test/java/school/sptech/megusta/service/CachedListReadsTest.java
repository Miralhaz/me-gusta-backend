package school.sptech.megusta.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheInterceptor;
import school.sptech.megusta.config.CacheNames;
import school.sptech.megusta.config.LoggingCacheErrorHandler;
import school.sptech.megusta.dto.insumo.InsumoResponse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Testes das listas cacheadas")
class CachedListReadsTest {

    @Test
    @DisplayName("GET /insumos: duas chamadas consultam os repositórios uma única vez")
    void insumosListIsServedFromCache() {
        InsumoRepository insumoRepository = mock(InsumoRepository.class);
        EntradaEstoqueRepository entradaEstoqueRepository = mock(EntradaEstoqueRepository.class);
        Insumo insumo = insumo(1, "Queijo");
        when(insumoRepository.findAll()).thenReturn(List.of(insumo));
        when(entradaEstoqueRepository.findByDtValidadeGreaterThanEqualOrderByDtValidadeAsc(
                org.mockito.ArgumentMatchers.any())).thenReturn(List.of(entrada(insumo, LocalDate.now().plusDays(10))));

        InsumoService service = new InsumoService(insumoRepository, mock(CategoriaInsumoRepository.class),
                mock(UnidadeMedidaRepository.class), mock(TipoStatusService.class), entradaEstoqueRepository,
                mock(SaidaEstoqueRepository.class), mock(StockListCacheInvalidator.class));
        InsumoService proxy = withCache(service, CacheNames.INSUMO_LISTA);

        List<InsumoResponse> primeira = proxy.listarInsumosParaAlertas();
        List<InsumoResponse> segunda = proxy.listarInsumosParaAlertas();

        assertEquals(1, primeira.size());
        assertEquals(primeira, segunda);
        assertNotNull(segunda.get(0).getProximaValidade());
        assertEquals(segunda.get(0).getDiasParaVencer(), primeira.get(0).getDiasParaVencer());
        verify(insumoRepository, times(1)).findAll();
        verify(entradaEstoqueRepository, times(1))
                .findByDtValidadeGreaterThanEqualOrderByDtValidadeAsc(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("GET /entradas-estoque: duas chamadas mapeiam as entidades uma única vez")
    void entradasListIsServedFromCache() {
        EntradaEstoqueRepository entradaEstoqueRepository = mock(EntradaEstoqueRepository.class);
        Insumo insumo = insumo(1, "Queijo");
        when(entradaEstoqueRepository.findAll()).thenReturn(List.of(entrada(insumo, LocalDate.now().plusDays(10))));

        EntradaEstoqueService service = new EntradaEstoqueService(entradaEstoqueRepository,
                mock(InsumoRepository.class), mock(UsuarioRepository.class), mock(FornecedorRepository.class),
                mock(TipoStatusRepository.class), mock(UnidadeMedidaRepository.class), mock(TipoStatusService.class),
                mock(StockListCacheInvalidator.class));
        EntradaEstoqueService proxy = withCache(service, CacheNames.ENTRADAS_ESTOQUE_LISTA);

        var primeira = proxy.listarEntradasParaAlertas();
        var segunda = proxy.listarEntradasParaAlertas();

        assertEquals(1, primeira.size());
        assertEquals(primeira, segunda);
        assertEquals(primeira.get(0).getDtValidade(), segunda.get(0).getDtValidade());
        verify(entradaEstoqueRepository, times(1)).findAll();
    }

    @SuppressWarnings("unchecked")
    private <T> T withCache(T target, String cacheName) {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(cacheName);
        CacheInterceptor interceptor = new CacheInterceptor();
        interceptor.setCacheOperationSource(new AnnotationCacheOperationSource());
        interceptor.setCacheManager(cacheManager);
        interceptor.setErrorHandler(new LoggingCacheErrorHandler());
        interceptor.afterSingletonsInstantiated();
        ProxyFactory factory = new ProxyFactory(target);
        factory.addAdvice(interceptor);
        return (T) factory.getProxy();
    }

    private Insumo insumo(Integer id, String nome) {
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
        insumo.setQtdAtual(10.0);
        insumo.setAtivo(true);
        insumo.setDtCadastro(LocalDateTime.of(2026, 11, 1, 10, 0));
        insumo.setCategoriaInsumo(categoria);
        insumo.setUnidadeMedida(unidade);
        insumo.setTipoStatus(status);
        return insumo;
    }

    private EntradaEstoque entrada(Insumo insumo, LocalDate validade) {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(1);
        fornecedor.setNome("Fornecedor");
        fornecedor.setCnpj("12345678000190");

        UnidadeMedida unidade = new UnidadeMedida();
        unidade.setId(1);
        unidade.setUnidade("kg");

        EntradaEstoque entrada = new EntradaEstoque();
        entrada.setId(1);
        entrada.setInsumo(insumo);
        entrada.setUsuario(usuario);
        entrada.setFornecedor(fornecedor);
        entrada.setTipoStatus(insumo.getTipoStatus());
        entrada.setUnidadeMedida(unidade);
        entrada.setQuantidadeAbsoluta(new BigDecimal("2.00"));
        entrada.setQuantidadeRelativa(new BigDecimal("1.00"));
        entrada.setDtEntrada(LocalDateTime.of(2026, 11, 1, 10, 0));
        entrada.setDtValidade(validade);
        return entrada;
    }
}
