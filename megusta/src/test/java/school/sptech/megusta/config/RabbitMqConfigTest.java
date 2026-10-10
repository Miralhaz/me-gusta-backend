package school.sptech.megusta.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Testes de contexto da configuração do RabbitMQ")
class RabbitMqConfigTest {

    @Autowired
    private TopicExchange megustaEstoqueExchange;

    @Autowired
    private MessageConverter messageConverter;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    @DisplayName("o contexto carrega com os beans de RabbitMQ")
    void contextoCarregaComOsBeansDeRabbitMQ() {
        Assertions.assertNotNull(megustaEstoqueExchange);
        Assertions.assertNotNull(messageConverter);
        Assertions.assertNotNull(rabbitTemplate);
        Assertions.assertNotNull(rabbitAdmin);
    }

    @Test
    @DisplayName("o exchange é um topic exchange durável com o nome configurado")
    void exchangeEhTopicDuravelComNomeConfigurado() {
        Assertions.assertEquals("megusta.estoque", megustaEstoqueExchange.getName());
        Assertions.assertEquals("topic", megustaEstoqueExchange.getType());
        Assertions.assertTrue(megustaEstoqueExchange.isDurable());
        Assertions.assertFalse(megustaEstoqueExchange.isAutoDelete());
    }

    @Test
    @DisplayName("o message converter é o Jackson2JsonMessageConverter")
    void messageConverterEhJackson2Json() {
        Assertions.assertTrue(messageConverter instanceof Jackson2JsonMessageConverter);
    }
}