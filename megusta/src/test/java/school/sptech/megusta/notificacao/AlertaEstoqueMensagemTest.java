package school.sptech.megusta.notificacao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayName("Testes do contrato JSON da AlertaEstoqueMensagem")
class AlertaEstoqueMensagemTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AlertaEstoque alerta(String nome, String codigo, Double qtdAtual, Double estoqueMinimo) {
        return new AlertaEstoque(nome, codigo, qtdAtual, estoqueMinimo);
    }

    @Test
    @DisplayName("deve expor telefone no nível raiz e alertas com os campos do contrato")
    void deveExporTelefoneNoNivelRaizEAlertasComCamposDoContrato() throws Exception {
        AlertaEstoqueMensagem mensagem = new AlertaEstoqueMensagem(
                "+5511999999999",
                List.of(alerta("Farinha de trigo", "INS-001", 2.0, 10.0))
        );

        String json = objectMapper.writeValueAsString(mensagem);

        JsonNode raiz = objectMapper.readTree(json);

        Assertions.assertEquals("+5511999999999", raiz.get("telefone").asText());

        JsonNode alertas = raiz.get("alertas");
        Assertions.assertNotNull(alertas);
        Assertions.assertTrue(alertas.isArray());
        Assertions.assertEquals(1, alertas.size());

        JsonNode item = alertas.get(0);
        Assertions.assertEquals("Farinha de trigo", item.get("nome").asText());
        Assertions.assertEquals("INS-001", item.get("codigoInsumo").asText());
        Assertions.assertEquals(2.0, item.get("qtdAtual").asDouble());
        Assertions.assertEquals(10.0, item.get("estoqueMinimo").asDouble());
    }

    @Test
    @DisplayName("deve conter todos os alertas recebidos na lista")
    void deveConterTodosOsAlertasRecebidos() throws Exception {
        AlertaEstoqueMensagem mensagem = new AlertaEstoqueMensagem(
                "+5511888888888",
                List.of(
                        alerta("Farinha de trigo", "INS-001", 2.0, 10.0),
                        alerta("Queijo mussarela", "INS-002", 1.5, 4.0)
                )
        );

        JsonNode raiz = objectMapper.readTree(objectMapper.writeValueAsString(mensagem));

        Assertions.assertEquals(2, raiz.get("alertas").size());
        Assertions.assertEquals("Farinha de trigo", raiz.get("alertas").get(0).get("nome").asText());
        Assertions.assertEquals("Queijo mussarela", raiz.get("alertas").get(1).get("nome").asText());
    }

    @Test
    @DisplayName("não deve conter texto de mensagem formatada para WhatsApp")
    void naoDeveConterTextoFormatadoDeWhatsApp() throws Exception {
        AlertaEstoqueMensagem mensagem = new AlertaEstoqueMensagem(
                "+5511999999999",
                List.of(alerta("Farinha de trigo", "INS-001", 2.0, 10.0))
        );

        String json = objectMapper.writeValueAsString(mensagem);

        Assertions.assertFalse(json.contains("Estoque baixo"));
        Assertions.assertFalse(json.toUpperCase().contains("WHATSAPP"));
    }
}
