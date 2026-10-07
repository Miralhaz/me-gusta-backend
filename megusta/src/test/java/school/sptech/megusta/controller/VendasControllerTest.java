package school.sptech.megusta.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import school.sptech.megusta.dto.planilha_vendas.BaixaInsumoResponse;
import school.sptech.megusta.exception.PlanilhaInvalidaException;
import school.sptech.megusta.service.VendasService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Testes de VendasController")
class VendasControllerTest {

    private VendasService vendasService;
    private VendasController controller;

    @BeforeEach
    void setup() {
        vendasService = mock(VendasService.class);
        controller = new VendasController(vendasService);
    }

    @Test
    @DisplayName("Deve retornar 400 para arquivo vazio sem persistir nada")
    void deveRetornar400ParaArquivoVazio() throws Exception {
        MockMultipartFile vazio = new MockMultipartFile(
                "planilha", "vazia.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);

        ResponseEntity<List<BaixaInsumoResponse>> resposta = controller.importar(vazio);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        Assertions.assertNull(resposta.getBody());
        verify(vendasService, never()).importarPlanilha(any());
    }

    @Test
    @DisplayName("Deve retornar 400 para extensão diferente de .xlsx sem persistir nada")
    void deveRetornar400ParaExtensaoInvalida() throws Exception {
        MockMultipartFile naoXlsx = new MockMultipartFile(
                "planilha", "planilha.xls", "application/vnd.ms-excel", "conteudo".getBytes());

        ResponseEntity<List<BaixaInsumoResponse>> resposta = controller.importar(naoXlsx);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        Assertions.assertNull(resposta.getBody());
        verify(vendasService, never()).importarPlanilha(any());
    }

    @Test
    @DisplayName("Deve retornar 200 com os insumos alterados após a importação")
    void deveRetornar200ComInsumosAlterados() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "planilha", "planilha.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "conteudo".getBytes());
        List<BaixaInsumoResponse> baixas = List.of(new BaixaInsumoResponse(
                "Farinha de Trigo", "FT-001", "kg",
                new BigDecimal("100.0"), new BigDecimal("12"), new BigDecimal("88.0")));

        when(vendasService.importarPlanilha(arquivo)).thenReturn(baixas);

        ResponseEntity<List<BaixaInsumoResponse>> resposta = controller.importar(arquivo);

        Assertions.assertEquals(HttpStatus.OK, resposta.getStatusCode());
        Assertions.assertEquals(baixas, resposta.getBody());
        verify(vendasService).importarPlanilha(arquivo);
    }

    @Test
    @DisplayName("Deve retornar 200 com lista vazia quando nada é alterado")
    void deveRetornar200ComListaVazia() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "planilha", "planilha.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "conteudo".getBytes());

        when(vendasService.importarPlanilha(arquivo)).thenReturn(List.of());

        ResponseEntity<List<BaixaInsumoResponse>> resposta = controller.importar(arquivo);

        Assertions.assertEquals(HttpStatus.OK, resposta.getStatusCode());
        Assertions.assertNotNull(resposta.getBody());
        Assertions.assertTrue(resposta.getBody().isEmpty());
    }

    @Test
    @DisplayName("Deve propagar o 400 sem corpo quando o layout não é suportado")
    void devePropagar400QuandoLayoutNaoSuportado() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "planilha", "pedidos.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "conteudo".getBytes());

        when(vendasService.importarPlanilha(arquivo)).thenThrow(PlanilhaInvalidaException.paraArquivo("pedidos.xlsx"));

        PlanilhaInvalidaException excecao =
                Assertions.assertThrows(PlanilhaInvalidaException.class, () -> controller.importar(arquivo));

        Assertions.assertTrue(excecao.getMessage().contains("pedidos.xlsx"));
        verify(vendasService).importarPlanilha(arquivo);
    }
}
