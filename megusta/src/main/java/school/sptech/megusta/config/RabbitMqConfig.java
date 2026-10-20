package school.sptech.megusta.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do RabbitMQ do produtor de alertas de estoque.
 *
 * <p>Declara um topic exchange durável com nome configurável
 * ({@code megusta.rabbitmq.exchange}) e registra o
 * {@link Jackson2JsonMessageConverter} usado para serializar as mensagens como
 * JSON. O consumidor é responsável por declarar e fazer o bind da própria fila.</p>
 */
@Configuration
public class RabbitMqConfig {

    @Value("${megusta.rabbitmq.exchange}")
    private String exchangeName;

    @Bean
    public TopicExchange megustaEstoqueExchange() {
        // nome, durable=true, autoDelete=false
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
