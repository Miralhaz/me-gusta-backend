package school.sptech.megusta.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import school.sptech.megusta.dto.entrada_estoque.EntradaEstoqueResponse;
import school.sptech.megusta.dto.insumo.InsumoResponse;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("Testes da configuração de cache Redis")
class CacheConfigTest {

    private final CacheConfig cacheConfig = new CacheConfig();

    @Test
    @DisplayName("CACHE_TTL_SECONDS configurado é refletido no TTL dos caches")
    void ttlOverrideIsReflectedInCacheConfiguration() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/application.properties")) {
            properties.load(in);
        }
        String expression = properties.getProperty("megusta.cache.ttl-seconds");
        assertEquals("${CACHE_TTL_SECONDS:60}", expression);

        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(
                new MapPropertySource("teste", Map.of("CACHE_TTL_SECONDS", "7")));
        long ttl = Long.parseLong(environment.resolveRequiredPlaceholders(expression));

        CacheManager cacheManager = cacheConfig.cacheManager(mock(RedisConnectionFactory.class), ttl);
        assertEquals(Duration.ofSeconds(7), ttlOf(cacheManager, CacheNames.INSUMO_LISTA));
        assertEquals(Duration.ofSeconds(7), ttlOf(cacheManager, CacheNames.ENTRADAS_ESTOQUE_LISTA));
    }

    @Test
    @DisplayName("TTL padrão é 60 segundos quando não há override")
    void defaultTtlIsSixtySeconds() {
        CacheManager cacheManager = cacheConfig.cacheManager(mock(RedisConnectionFactory.class), 60);
        assertEquals(Duration.ofSeconds(60), ttlOf(cacheManager, CacheNames.INSUMO_LISTA));
        assertEquals(Duration.ofSeconds(60), ttlOf(cacheManager, CacheNames.ENTRADAS_ESTOQUE_LISTA));
    }

    @Test
    @DisplayName("Serializador preserva LocalDate/LocalDateTime em DTOs de resposta")
    void serializerRoundTripsResponseDtosWithJavaTimes() {
        GenericJackson2JsonRedisSerializer serializer = CacheConfig.valueSerializer();

        EntradaEstoqueResponse entrada = new EntradaEstoqueResponse();
        entrada.setId(1);
        entrada.setQuantidadeAbsoluta(new BigDecimal("2.50"));
        entrada.setDtEntrada(LocalDateTime.of(2026, 11, 20, 14, 30));
        entrada.setDtValidade(LocalDate.of(2026, 12, 25));
        entrada.setDtPedido(LocalDate.of(2026, 11, 18));

        InsumoResponse insumo = new InsumoResponse();
        insumo.setId(2);
        insumo.setProximaValidade(LocalDate.of(2026, 12, 25));
        insumo.setDiasParaVencer(33L);

        byte[] bytes = serializer.serialize(new java.util.ArrayList<>(List.of(entrada, insumo)));
        Object result = serializer.deserialize(bytes);

        assertInstanceOf(List.class, result);
        @SuppressWarnings("unchecked")
        List<Object> lista = (List<Object>) result;
        assertEquals(2, lista.size());

        EntradaEstoqueResponse entradaCopiada = (EntradaEstoqueResponse) lista.get(0);
        assertEquals(entrada.getDtEntrada(), entradaCopiada.getDtEntrada());
        assertEquals(entrada.getDtValidade(), entradaCopiada.getDtValidade());
        assertEquals(entrada.getDtPedido(), entradaCopiada.getDtPedido());
        assertEquals(entrada.getQuantidadeAbsoluta(), entradaCopiada.getQuantidadeAbsoluta());

        InsumoResponse insumoCopiado = (InsumoResponse) lista.get(1);
        assertEquals(insumo.getProximaValidade(), insumoCopiado.getProximaValidade());
        assertEquals(insumo.getDiasParaVencer(), insumoCopiado.getDiasParaVencer());
    }

    private Duration ttlOf(CacheManager cacheManager, String name) {
        Cache cache = cacheManager.getCache(name);
        assertNotNull(cache);
        assertInstanceOf(RedisCache.class, cache);
        return ((RedisCache) cache).getCacheConfiguration().getTtl();
    }
}
