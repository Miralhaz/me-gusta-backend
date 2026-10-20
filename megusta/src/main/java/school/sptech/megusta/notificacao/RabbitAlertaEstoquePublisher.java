package school.sptech.megusta.notificacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adapter de saída que publica o alerta de estoque no RabbitMQ usando Spring AMQP.
 *
 * <p>Serializa a {@link AlertaEstoqueMensagem} como JSON (via
 * {@code Jackson2JsonMessageConverter} configurado no
 * {@code RabbitTemplate}) e publica no exchange/routing key configurados.</p>
 */
@Component
public class RabbitAlertaEstoquePublisher implements AlertaEstoquePublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitAlertaEstoquePublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public RabbitAlertaEstoquePublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${megusta.rabbitmq.exchange}") String exchange,
            @Value("${megusta.rabbitmq.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void publicar(String telefone, List<AlertaEstoque> alertas) {
        AlertaEstoqueMensagem mensagem = new AlertaEstoqueMensagem(telefone, alertas);

        rabbitTemplate.convertAndSend(exchange, routingKey, mensagem);

        log.info("Alerta de estoque publicado no exchange '{}' (routing key '{}') para o telefone {}.",
                exchange, routingKey, telefone);
    }
}
