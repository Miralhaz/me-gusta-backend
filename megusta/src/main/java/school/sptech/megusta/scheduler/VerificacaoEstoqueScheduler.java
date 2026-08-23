package school.sptech.megusta.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.notificacao.AlertaEstoque;
import school.sptech.megusta.notificacao.NotificacaoEstoqueService;
import school.sptech.megusta.notificacao.WhatsAppSenderPort;
import school.sptech.megusta.repository.InsumoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class VerificacaoEstoqueScheduler {

    private static final Logger log = LoggerFactory.getLogger(VerificacaoEstoqueScheduler.class);

    private final InsumoRepository insumoRepository;
    private final NotificacaoEstoqueService notificacaoEstoqueService;

    public VerificacaoEstoqueScheduler(InsumoRepository insumoRepository,
                                       WhatsAppSenderPort whatsappSender) {
        this.insumoRepository = insumoRepository;
        this.notificacaoEstoqueService = new NotificacaoEstoqueService(whatsappSender);
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void verificarEstoqueMinimo() {
        log.info("[{}] Iniciando verificação de estoque mínimo...", LocalDateTime.now());

        List<AlertaEstoque> alertas = insumoRepository.findAll().stream()
                .filter(Insumo::isAtivo)
                .map(i -> new AlertaEstoque(i.getNome(), i.getCodigoInsumo(), i.getQtdAtual(), i.getEstoqueMinimo()))
                .toList();

        int notificados = notificacaoEstoqueService.notificarAbaixoMinimo(alertas);

        if (notificados > 0) {
            log.warn("{} insumo(s) abaixo do estoque mínimo — notificação(ões) despachada(s).", notificados);
        } else {
            log.info("Todos os insumos estão com estoque adequado.");
        }

        log.info("Verificação de estoque concluída.");
    }
}
