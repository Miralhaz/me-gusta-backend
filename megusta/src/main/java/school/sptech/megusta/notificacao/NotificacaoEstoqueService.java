package school.sptech.megusta.notificacao;

import java.util.List;

public class NotificacaoEstoqueService {

    private final WhatsAppSenderPort whatsAppSender;

    public NotificacaoEstoqueService(WhatsAppSenderPort whatsAppSender) {
        this.whatsAppSender = whatsAppSender;
    }

    public int notificarAbaixoMinimo(List<AlertaEstoque> alertas) {
        List<AlertaEstoque> abaixoDoMinimo = alertas.stream()
                .filter(alerta -> alerta.qtdAtual() < alerta.estoqueMinimo())
                .toList();

        for (AlertaEstoque alerta : abaixoDoMinimo) {
            try {
                whatsAppSender.enviar(formatarMensagem(alerta));
            } catch (Exception e) {
                // Isolamento de falha: um envio que falhou não interrompe os demais insumos.
            }
        }

        return abaixoDoMinimo.size();
    }

    private String formatarMensagem(AlertaEstoque alerta) {
        return "⚠️ Estoque baixo: %s (%s) | Atual: %s | Mínimo: %s".formatted(
                alerta.nome(),
                alerta.codigoInsumo(),
                alerta.qtdAtual(),
                alerta.estoqueMinimo()
        );
    }
}
