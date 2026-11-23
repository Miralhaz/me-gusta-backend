package school.sptech.megusta.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.sptech.megusta.dto.entrada_estoque.EntradaEstoqueResponse;
import school.sptech.megusta.dto.insumo.InsumoResponse;
import school.sptech.megusta.service.EntradaEstoqueService;
import school.sptech.megusta.service.InsumoService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Testes dos controllers das listas cacheadas")
class ListControllersCacheTest {

    @Test
    @DisplayName("GET /insumos retorna 204 para lista vazia usando o método cacheado")
    void insumosEmptyReturns204() {
        InsumoService service = mock(InsumoService.class);
        InsumoController controller = new InsumoController(service);
        when(service.listarInsumosParaAlertas()).thenReturn(List.of());

        ResponseEntity<List<InsumoResponse>> resposta = controller.listar();

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(service).listarInsumosParaAlertas();
    }

    @Test
    @DisplayName("GET /insumos retorna 200 com proximaValidade e diasParaVencer preenchidos")
    void insumosReturnsComputedExpiryFields() {
        InsumoService service = mock(InsumoService.class);
        InsumoController controller = new InsumoController(service);
        InsumoResponse insumo = new InsumoResponse();
        insumo.setId(1);
        insumo.setProximaValidade(LocalDate.now().plusDays(5));
        insumo.setDiasParaVencer(5L);
        when(service.listarInsumosParaAlertas()).thenReturn(List.of(insumo));

        ResponseEntity<List<InsumoResponse>> resposta = controller.listar();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1, resposta.getBody().size());
        assertEquals(insumo.getProximaValidade(), resposta.getBody().get(0).getProximaValidade());
        assertEquals(5L, resposta.getBody().get(0).getDiasParaVencer());
        verify(service).listarInsumosParaAlertas();
    }

    @Test
    @DisplayName("GET /entradas-estoque retorna 204 para lista vazia usando o método cacheado")
    void entradasEmptyReturns204() {
        EntradaEstoqueService service = mock(EntradaEstoqueService.class);
        EntradaEstoqueController controller = new EntradaEstoqueController(service);
        when(service.listarEntradasParaAlertas()).thenReturn(List.of());

        ResponseEntity<List<EntradaEstoqueResponse>> resposta = controller.listar();

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(service).listarEntradasParaAlertas();
    }

    @Test
    @DisplayName("GET /entradas-estoque retorna 200 com os DTOs do método cacheado")
    void entradasReturnsCachedDtos() {
        EntradaEstoqueService service = mock(EntradaEstoqueService.class);
        EntradaEstoqueController controller = new EntradaEstoqueController(service);
        EntradaEstoqueResponse entrada = new EntradaEstoqueResponse();
        entrada.setId(1);
        when(service.listarEntradasParaAlertas()).thenReturn(List.of(entrada));

        ResponseEntity<List<EntradaEstoqueResponse>> resposta = controller.listar();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().size());
        verify(service).listarEntradasParaAlertas();
    }
}
