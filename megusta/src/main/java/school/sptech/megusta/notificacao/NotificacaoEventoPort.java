package school.sptech.megusta.notificacao;

/**
 * Porta de saída reservada para a futura integração com RabbitMQ:
 * publicará eventos de notificação de estoque quando o domínio for
 * extraído para o microservice de notificações.
 *
 * Sem implementação e sem wiring nesta fase (POC).
 */
public interface NotificacaoEventoPort {

    void publicar(AlertaEstoque alerta);
}
