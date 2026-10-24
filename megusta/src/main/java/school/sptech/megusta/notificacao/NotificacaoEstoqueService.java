package school.sptech.megusta.notificacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Serviço de aplicação responsável por decidir <em>o que</em> e <em>para quem</em>
 * publicar. Faz o fan-out de uma mensagem por telefone recebido, filtrando
 * apenas os insumos abaixo do estoque mínimo.
 *
 * <p>Sem dependência de Spring: depende apenas da porta de saída.</p>
 */
public class NotificacaoEstoqueService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoEstoqueService.class);

    private final AlertaEstoquePublisherPort alertaEstoquePublisher;

    public NotificacaoEstoqueService(AlertaEstoquePublisherPort alertaEstoquePublisher) {
        this.alertaEstoquePublisher = alertaEstoquePublisher;
    }

    /**
     * Publica, para cada telefone, uma mensagem com os insumos abaixo do mínimo.
     *
     * @param telefones telefones dos destinatários cadastrados
     * @param alertas   insumos candidatos (o filtro ocorre aqui)
     * @return quantidade de mensagens publicadas com sucesso
     */
    public int notificarAbaixoMinimo(List<String> telefones, List<AlertaEstoque> alertas) {
        List<AlertaEstoque> abaixoDoMinimo = alertas.stream()
                .filter(alerta -> alerta.qtdAtual() < alerta.estoqueMinimo())
                .toList();

        if (abaixoDoMinimo.isEmpty()) {
            return 0;
        }

        int publicadas = 0;
        for (String telefone : telefones) {
            if (telefone == null || telefone.isBlank()) {
                log.warn("Destinatário sem telefone (nulo/vazio) ignorado na publicação de alerta de estoque.");
                continue;
            }

            try {
                alertaEstoquePublisher.publicar(telefone, abaixoDoMinimo);
                publicadas++;
            } catch (Exception e) {
                // Isolamento de falha: um destinatário que falhou não interrompe os demais.
                log.error("Falha ao publicar alerta de estoque para o telefone {}.", telefone, e);
            }
        }

        return publicadas;
    }
}
