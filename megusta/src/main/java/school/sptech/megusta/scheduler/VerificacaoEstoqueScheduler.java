package school.sptech.megusta.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.repository.InsumoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class VerificacaoEstoqueScheduler {

    private static final Logger log = LoggerFactory.getLogger(VerificacaoEstoqueScheduler.class);

    private final InsumoRepository insumoRepository;

    public VerificacaoEstoqueScheduler(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void verificarEstoqueMinimo() {
        log.info("[{}] Iniciando verificação de estoque mínimo...", LocalDateTime.now());

        List<Insumo> todosInsumos = insumoRepository.findAll();

        List<Insumo> insumosAbaixoMinimo = todosInsumos.stream()
                .filter(Insumo::isAtivo)
                .filter(i -> i.getQtdAtual() < i.getEstoqueMinimo())
                .toList();

        if (insumosAbaixoMinimo.isEmpty()) {
            log.info("Todos os insumos estão com estoque adequado.");
        } else {
            log.warn("*** {} insumo(s) abaixo do estoque mínimo:", insumosAbaixoMinimo.size());
            insumosAbaixoMinimo.forEach(i ->
                    log.warn("  - {} (Código: {}) | Atual: {} | Mínimo: {}",
                            i.getNome(),
                            i.getCodigoInsumo(),
                            i.getQtdAtual(),
                            i.getEstoqueMinimo()
                    )
            );
        }

        log.info("Verificação de estoque concluída.");
    }
}