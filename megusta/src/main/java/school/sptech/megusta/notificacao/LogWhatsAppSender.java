package school.sptech.megusta.notificacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LogWhatsAppSender implements WhatsAppSenderPort {

    private static final Logger log = LoggerFactory.getLogger(LogWhatsAppSender.class);

    private final String destinatario;

    public LogWhatsAppSender(
            @Value("${megusta.notificacao.whatsapp.destinatario}") String destinatario
    ) {
        this.destinatario = destinatario;
    }

    @Override
    public void enviar(String mensagem) {
        try {
            log.warn("[POC] Enviando WhatsApp para {}: {}", destinatario, mensagem);
        } catch (RuntimeException e) {
            log.error("[POC] Falha ao enviar WhatsApp para {}: {}", destinatario, mensagem, e);
            throw e;
        }
    }
}
