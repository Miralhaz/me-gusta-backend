package school.sptech.megusta.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.notificacao.AlertaEstoque;
import school.sptech.megusta.notificacao.AlertaEstoquePublisherPort;
import school.sptech.megusta.notificacao.NotificacaoEstoqueService;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class VerificacaoEstoqueScheduler {

    private static final Logger log = LoggerFactory.getLogger(VerificacaoEstoqueScheduler.class);

    private final InsumoRepository insumoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoEstoqueService notificacaoEstoqueService;

    public VerificacaoEstoqueScheduler(InsumoRepository insumoRepository,
                                       UsuarioRepository usuarioRepository,
                                       AlertaEstoquePublisherPort alertaEstoquePublisher) {
        this.insumoRepository = insumoRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoEstoqueService = new NotificacaoEstoqueService(alertaEstoquePublisher);
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void verificarEstoqueMinimo() {
        log.info("[{}] Iniciando verificação de estoque mínimo...", LocalDateTime.now());

        List<AlertaEstoque> alertas = insumoRepository.findAll().stream()
                .filter(Insumo::isAtivo)
                .map(i -> new AlertaEstoque(i.getNome(), i.getCodigoInsumo(), i.getQtdAtual(), i.getEstoqueMinimo()))
                .toList();

        List<String> telefones = usuarioRepository.findTopByOrderByIdDesc()
                .map(Usuario::getTelefone)
                .map(List::of)
                .orElseGet(List::of);

        int publicadas = notificacaoEstoqueService.notificarAbaixoMinimo(telefones, alertas);

        if (publicadas > 0) {
            log.warn("{} mensagen(s) de alerta de estoque publicada(s).", publicadas);
        } else {
            log.info("Nenhum alerta de estoque publicado.");
        }

        log.info("Verificação de estoque concluída.");
    }
}