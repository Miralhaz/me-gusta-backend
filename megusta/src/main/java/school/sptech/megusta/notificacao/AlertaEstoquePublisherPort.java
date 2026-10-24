package school.sptech.megusta.notificacao;

import java.util.List;

/**
 * Porta de saída do domínio para publicação dos alertas de estoque.
 *
 * <p>Não possui dependência de Spring: a implementação concreta (adapter) é
 * responsável por serializar e publicar no broker (RabbitMQ).</p>
 */
public interface AlertaEstoquePublisherPort {

    /**
     * Publica, para um único destinatário, a lista de insumos abaixo do estoque mínimo.
     *
     * @param telefone telefone do destinatário (nível raiz da mensagem)
     * @param alertas  insumos abaixo do estoque mínimo
     */
    void publicar(String telefone, List<AlertaEstoque> alertas);
}
