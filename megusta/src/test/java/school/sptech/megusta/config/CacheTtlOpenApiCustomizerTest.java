package school.sptech.megusta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import school.sptech.megusta.controller.EntradaEstoqueController;
import school.sptech.megusta.controller.InsumoController;

@DisplayName("Testes da documentação de freshness do cache no OpenAPI")
class CacheTtlOpenApiCustomizerTest {

    @Test
    @DisplayName("Adiciona TTL e nota de campos calculados às duas listas cacheadas")
    void customiseAddsTtlAndComputedFieldsNote() {
        OpenAPI openApi = new OpenAPI().paths(new Paths()
                .addPathItem(CacheTtlOpenApiCustomizer.PATH_INSUMOS,
                        new PathItem().get(new Operation().description("Base insumos.")))
                .addPathItem(CacheTtlOpenApiCustomizer.PATH_ENTRADAS_ESTOQUE,
                        new PathItem().get(new Operation().description("Base entradas.")))
                .addPathItem("/insumos/{id}",
                        new PathItem().get(new Operation().description("Outro endpoint."))));

        new CacheTtlOpenApiCustomizer(45).customise(openApi);

        String insumos = openApi.getPaths().get(CacheTtlOpenApiCustomizer.PATH_INSUMOS).getGet().getDescription();
        assertTrue(insumos.contains("Base insumos."));
        assertTrue(insumos.contains("proximaValidade"));
        assertTrue(insumos.contains("diasParaVencer"));
        assertTrue(insumos.contains("de cache"));
        assertTrue(insumos.contains("45 segundos"));
        assertTrue(insumos.contains("CACHE_TTL_SECONDS"));

        String entradas = openApi.getPaths()
                .get(CacheTtlOpenApiCustomizer.PATH_ENTRADAS_ESTOQUE).getGet().getDescription();
        assertTrue(entradas.contains("Base entradas."));
        assertTrue(entradas.contains("45 segundos"));
        assertTrue(entradas.contains("CACHE_TTL_SECONDS"));

        assertEquals("Outro endpoint.",
                openApi.getPaths().get("/insumos/{id}").getGet().getDescription());
    }

    @Test
    @DisplayName("Customizer usa o valor configurado de megusta.cache.ttl-seconds")
    void customizerUsesConfiguredTtlValue() throws Exception {
        Value value = CacheTtlOpenApiCustomizer.class.getDeclaredConstructors()[0]
                .getParameters()[0].getAnnotation(Value.class);
        assertEquals("${megusta.cache.ttl-seconds:60}", value.value());

        Method insumos = InsumoController.class.getMethod("listar");
        io.swagger.v3.oas.annotations.Operation insumosOp =
                AnnotationUtils.findAnnotation(insumos, io.swagger.v3.oas.annotations.Operation.class);
        assertTrue(insumosOp.description().contains("datas de validade"));

        Method entradas = EntradaEstoqueController.class.getMethod("listar");
        io.swagger.v3.oas.annotations.Operation entradasOp =
                AnnotationUtils.findAnnotation(entradas, io.swagger.v3.oas.annotations.Operation.class);
        assertTrue(entradasOp.description().contains("entradas de estoque"));
    }
}
