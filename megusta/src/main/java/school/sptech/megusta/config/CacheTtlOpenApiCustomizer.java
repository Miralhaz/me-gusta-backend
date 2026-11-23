package school.sptech.megusta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Documenta o limite de desatualização das duas listas cacheadas diretamente no
 * documento OpenAPI gerado, usando o TTL efetivamente configurado para o gerenciador de cache.
 */
@Component
public class CacheTtlOpenApiCustomizer implements OpenApiCustomizer {

    static final String PATH_INSUMOS = "/insumos";
    static final String PATH_ENTRADAS_ESTOQUE = "/entradas-estoque";

    private final long ttlSeconds;

    public CacheTtlOpenApiCustomizer(@Value("${megusta.cache.ttl-seconds:60}") long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    public void customise(OpenAPI openApi) {
        appendNote(openApi, PATH_INSUMOS,
                "Esta lista é servida de cache. Os campos proximaValidade e diasParaVencer são calculados no momento "
                        + "em que a resposta é armazenada e permanecem inalterados até a invalidação por uma escrita "
                        + "ou a expiração do cache.");
        appendNote(openApi, PATH_ENTRADAS_ESTOQUE,
                "Esta lista é servida de cache. Alterações de estoque são refletidas apenas após a invalidação por "
                        + "uma escrita ou a expiração do cache.");
    }

    private void appendNote(OpenAPI openApi, String path, String note) {
        if (openApi.getPaths() == null) {
            return;
        }
        PathItem pathItem = openApi.getPaths().get(path);
        if (pathItem == null || pathItem.getGet() == null) {
            return;
        }
        Operation operation = pathItem.getGet();
        String ttlNote = " O limite máximo de desatualização é de " + ttlSeconds
                + " segundos (TTL configurável via CACHE_TTL_SECONDS).";
        String base = operation.getDescription() == null ? "" : operation.getDescription().trim();
        operation.setDescription(base.isEmpty() ? (note + ttlNote) : (base + " " + note + ttlNote));
    }
}
