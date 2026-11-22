package school.sptech.megusta.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.megusta.config.CacheNames;
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
import school.sptech.megusta.repository.TipoStatusRepository;
import school.sptech.megusta.repository.UnidadeMedidaRepository;
import school.sptech.megusta.repository.UsuarioRepository;
import school.sptech.megusta.service.EntradaEstoqueService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
@DisplayName("Integração das listas cacheadas")
class ListEndpointsCacheIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    @SpyBean
    private InsumoRepository insumoRepository;

    @SpyBean
    private EntradaEstoqueRepository entradaEstoqueRepository;

    @Autowired
    private CategoriaInsumoRepository categoriaRepository;

    @Autowired
    private UnidadeMedidaRepository unidadeRepository;

    @Autowired
    private TipoStatusRepository tipoStatusRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private EntradaEstoqueService entradaEstoqueService;

    private Insumo insumo;
    private Usuario usuario;
    private Fornecedor fornecedor;
    private TipoStatus status;
    private UnidadeMedida unidade;

    @TestConfiguration
    static class TestCacheConfig {
        @Bean
        @Primary
        CacheManager testCacheManager() {
            return new ConcurrentMapCacheManager(
                    CacheNames.INSUMO_LISTA, CacheNames.ENTRADAS_ESTOQUE_LISTA);
        }
    }

    @BeforeEach
    void preparar() {
        cacheManager.getCache(CacheNames.INSUMO_LISTA).clear();
        cacheManager.getCache(CacheNames.ENTRADAS_ESTOQUE_LISTA).clear();

        entradaEstoqueRepository.deleteAll();
        insumoRepository.deleteAll();
        usuarioRepository.deleteAll();
        fornecedorRepository.deleteAll();
        categoriaRepository.deleteAll();
        unidadeRepository.deleteAll();
        tipoStatusRepository.deleteAll();

        CategoriaInsumo categoria = new CategoriaInsumo();
        categoria.setNome("Laticínios");
        categoria = categoriaRepository.save(categoria);

        unidade = new UnidadeMedida();
        unidade.setUnidade("kg");
        unidade = unidadeRepository.save(unidade);

        status = new TipoStatus();
        status.setNome("OK");
        status = tipoStatusRepository.save(status);

        insumo = new Insumo();
        insumo.setNome("Queijo");
        insumo.setCodigoInsumo("IN-1");
        insumo.setEstoqueMinimo(1.0);
        insumo.setQtdAtual(10.0);
        insumo.setAtivo(true);
        insumo.setCategoriaInsumo(categoria);
        insumo.setUnidadeMedida(unidade);
        insumo.setTipoStatus(status);
        insumo = insumoRepository.save(insumo);

        usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senha");
        usuario.setTelefone("11999999999");
        usuario = usuarioRepository.save(usuario);

        fornecedor = new Fornecedor();
        fornecedor.setNome("Fornecedor");
        fornecedor.setCnpj("11444777000161");
        fornecedor.setTelefone("11999999999");
        fornecedor.setAtivo(true);
        fornecedor = fornecedorRepository.save(fornecedor);

        EntradaEstoque entrada = novaEntrada(new BigDecimal("2.00"));
        entradaEstoqueRepository.save(entrada);
    }

    @Test
    @DisplayName("Duas leituras de cada lista consultam o banco uma vez; escrita evicta ambas")
    void listasSaoCacheadasEInvalidadasPorEscrita() throws Exception {
        mockMvc.perform(get("/insumos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/insumos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(insumoRepository, times(1)).findAll();

        mockMvc.perform(get("/entradas-estoque").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/entradas-estoque").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(entradaEstoqueRepository, times(1)).findAll();

        EntradaEstoque nova = novaEntrada(new BigDecimal("3.00"));
        entradaEstoqueService.cadastrar(nova);

        mockMvc.perform(get("/insumos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].quantidadeAtual").value(13.0));
        mockMvc.perform(get("/entradas-estoque").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(insumoRepository, times(2)).findAll();
        verify(entradaEstoqueRepository, times(2)).findAll();
    }

    private EntradaEstoque novaEntrada(BigDecimal quantidade) {
        EntradaEstoque entrada = new EntradaEstoque();
        entrada.setInsumo(insumo);
        entrada.setUsuario(usuario);
        entrada.setFornecedor(fornecedor);
        entrada.setTipoStatus(status);
        entrada.setUnidadeMedida(unidade);
        entrada.setQuantidadeAbsoluta(quantidade);
        entrada.setQuantidadeRelativa(BigDecimal.ONE);
        entrada.setDtEntrada(LocalDateTime.of(2026, 11, 20, 10, 0));
        entrada.setDtValidade(LocalDate.now().plusDays(10));
        return entrada;
    }
}
