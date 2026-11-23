package school.sptech.megusta.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import school.sptech.megusta.config.CacheNames;


@Component
public class StockListCacheInvalidator {

    private final CacheManager cacheManager;

    public StockListCacheInvalidator(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void evictInsumosLista() {
        evict(CacheNames.INSUMO_LISTA);
    }

    public void evictEntradasEstoqueLista() {
        evict(CacheNames.ENTRADAS_ESTOQUE_LISTA);
    }

    public void evictBothLists() {
        evict(CacheNames.INSUMO_LISTA);
        evict(CacheNames.ENTRADAS_ESTOQUE_LISTA);
    }

    private void evict(String cacheName) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    clear(cacheName);
                }
            });
        } else {
            clear(cacheName);
        }
    }

    private void clear(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
}
