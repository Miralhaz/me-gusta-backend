package school.sptech.megusta.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Documentação OpenAPI das listas cacheadas")
class ApiDocsCacheDescriptionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /v3/api-docs descreve TTL e campos calculados das listas cacheadas")
    void apiDocsDescribeCacheFreshness() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/insumos'].get.description", containsString("proximaValidade")))
                .andExpect(jsonPath("$.paths['/insumos'].get.description", containsString("diasParaVencer")))
                .andExpect(jsonPath("$.paths['/insumos'].get.description", containsString("segundos")))
                .andExpect(jsonPath("$.paths['/insumos'].get.description", containsString("CACHE_TTL_SECONDS")))
                .andExpect(jsonPath("$.paths['/entradas-estoque'].get.description", containsString("cache")))
                .andExpect(jsonPath("$.paths['/entradas-estoque'].get.description", containsString("segundos")));
    }
}
