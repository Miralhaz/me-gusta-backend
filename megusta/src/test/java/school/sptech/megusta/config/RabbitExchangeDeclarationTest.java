package school.sptech.megusta.config;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Declaração do exchange no broker (com Channel mockado, sem broker real)")
class RabbitExchangeDeclarationTest {

    @Test
    @DisplayName("o RabbitAdmin declara o exchange tópico durável configurado")
    void deveDeclararOExchangeTopicDuravel() throws Exception {
        Channel channel = mock(Channel.class);

        Connection connection = mock(Connection.class);
        when(connection.createChannel(anyBoolean())).thenReturn(channel);
        when(connection.isOpen()).thenReturn(true);

        ConnectionFactory connectionFactory = new ConnectionFactory() {
            @Override
            public Connection createConnection() {
                return connection;
            }

            @Override
            public String getHost() {
                return "localhost";
            }

            @Override
            public int getPort() {
                return 5672;
            }

            @Override
            public String getVirtualHost() {
                return "/";
            }

            @Override
            public String getUsername() {
                return "guest";
            }

            @Override
            public void addConnectionListener(ConnectionListener listener) {
                // no-op
            }

            @Override
            public boolean removeConnectionListener(ConnectionListener listener) {
                return false;
            }

            @Override
            public void clearConnectionListeners() {
                // no-op
            }
        };

        RabbitMqConfig config = new RabbitMqConfig();
        ReflectionTestUtils.setField(config, "exchangeName", "megusta.estoque");
        TopicExchange exchange = config.megustaEstoqueExchange();

        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.declareExchange(exchange);

        verify(channel).exchangeDeclare(
                eq("megusta.estoque"),
                eq("topic"),
                eq(true),
                eq(false),
                eq(false),
                anyMap()
        );
    }
}