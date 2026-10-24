package school.sptech.megusta.notificacao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe RabbitAlertaEstoquePublisher")
class RabbitAlertaEstoquePublisherTest {

    private static final String EXCHANGE = "megusta.estoque";
    private static final String ROUTING_KEY = "alerta.estoque";

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitAlertaEstoquePublisher publisher() {
        return new RabbitAlertaEstoquePublisher(rabbitTemplate, EXCHANGE, ROUTING_KEY);
    }

    private AlertaEstoque alerta(String nome, String codigo, Double qtdAtual, Double estoqueMinimo) {
        return new AlertaEstoque(nome, codigo, qtdAtual, estoqueMinimo);
    }

    @Test
    @DisplayName("deve publicar no exchange e routing key configurados com a mensagem do contrato")
    void devePublicarNoExchangeERoutingKeyConfigurados() {
        AlertaEstoque alerta = alerta("Farinha de trigo", "INS-001", 2.0, 10.0);

        publisher().publicar("+5511999999999", List.of(alerta));

        ArgumentCaptor<AlertaEstoqueMensagem> captor = ArgumentCaptor.forClass(AlertaEstoqueMensagem.class);
        Mockito.verify(rabbitTemplate).convertAndSend(Mockito.eq(EXCHANGE), Mockito.eq(ROUTING_KEY), captor.capture());

        AlertaEstoqueMensagem mensagem = captor.getValue();
        Assertions.assertEquals("+5511999999999", mensagem.telefone());
        Assertions.assertEquals(List.of(alerta), mensagem.alertas());
    }

    @Test
    @DisplayName("o Jackson2JsonMessageConverter serializa a mensagem em JSON no formato do contrato (sem broker)")
    void jackson2JsonMessageConverterSerializaNoFormatoDoContrato() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        AlertaEstoqueMensagem mensagem = new AlertaEstoqueMensagem(
                "+5511999999999",
                List.of(alerta("Farinha de trigo", "INS-001", 2.0, 10.0))
        );

        Message message = converter.toMessage(mensagem, new MessageProperties());

        JsonNode raiz = objectMapper.readTree(message.getBody());

        Assertions.assertEquals("+5511999999999", raiz.get("telefone").asText());

        JsonNode item = raiz.get("alertas").get(0);
        Assertions.assertEquals("Farinha de trigo", item.get("nome").asText());
        Assertions.assertEquals("INS-001", item.get("codigoInsumo").asText());
        Assertions.assertEquals(2.0, item.get("qtdAtual").asDouble());
        Assertions.assertEquals(10.0, item.get("estoqueMinimo").asDouble());
    }
}