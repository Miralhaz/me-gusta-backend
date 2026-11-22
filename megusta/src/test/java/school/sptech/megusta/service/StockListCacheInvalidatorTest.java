package school.sptech.megusta.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import school.sptech.megusta.config.CacheNames;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do invalidator das listas cacheadas")
class StockListCacheInvalidatorTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache insumoCache;

    @Mock
    private Cache entradasCache;

    @InjectMocks
    private StockListCacheInvalidator invalidator;

    @AfterEach
    void limparTransacao() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    @Test
    @DisplayName("evictInsumosLista limpa somente o cache dos insumos")
    void evictInsumosOnly() {
        registrarCaches();

        invalidator.evictInsumosLista();

        verify(insumoCache).clear();
        verify(entradasCache, never()).clear();
    }

    @Test
    @DisplayName("evictEntradasEstoqueLista limpa somente o cache das entradas")
    void evictEntradasOnly() {
        registrarCaches();

        invalidator.evictEntradasEstoqueLista();

        verify(entradasCache).clear();
        verify(insumoCache, never()).clear();
    }

    @Test
    @DisplayName("evictBothLists limpa os dois caches e nenhum outro")
    void evictBoth() {
        registrarCaches();

        invalidator.evictBothLists();

        verify(insumoCache).clear();
        verify(entradasCache).clear();
    }

    @Test
    @DisplayName("com transação ativa a limpeza acontece apenas após o commit")
    void evictionWaitsForCommit() {
        registrarCaches();
        iniciarTransacao();

        invalidator.evictBothLists();

        verify(insumoCache, never()).clear();
        verify(entradasCache, never()).clear();

        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.afterCommit();
        }

        verify(insumoCache).clear();
        verify(entradasCache).clear();
    }

    @Test
    @DisplayName("rollback não dispara a limpeza dos caches")
    void rollbackDoesNotEvict() {
        registrarCaches();
        iniciarTransacao();

        invalidator.evictBothLists();
        TransactionSynchronizationManager.clearSynchronization();

        verify(insumoCache, never()).clear();
        verify(entradasCache, never()).clear();
    }

    private void registrarCaches() {
        lenient().when(cacheManager.getCache(CacheNames.INSUMO_LISTA)).thenReturn(insumoCache);
        lenient().when(cacheManager.getCache(CacheNames.ENTRADAS_ESTOQUE_LISTA)).thenReturn(entradasCache);
    }

    private void iniciarTransacao() {
        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);
    }
}
