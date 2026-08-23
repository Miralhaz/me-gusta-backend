package school.sptech.megusta.scheduler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.notificacao.WhatsAppSenderPort;
import school.sptech.megusta.repository.InsumoRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe VerificacaoEstoqueScheduler")
class VerificacaoEstoqueSchedulerTest {

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private WhatsAppSenderPort whatsAppSender;

    private Insumo insumo(String nome, String codigo, Double qtdAtual, Double estoqueMinimo, boolean ativo) {
        Insumo insumo = new Insumo();
        insumo.setNome(nome);
        insumo.setCodigoInsumo(codigo);
        insumo.setQtdAtual(qtdAtual);
        insumo.setEstoqueMinimo(estoqueMinimo);
        insumo.setAtivo(ativo);
        return insumo;
    }

    @Nested
    @DisplayName("Método verificarEstoqueMinimo")
    class verificarEstoqueMinimo {

        @Test
        @DisplayName("deve notificar apenas insumos ativos abaixo do mínimo")
        void deveNotificarApenasAtivosAbaixoDoMinimo() {
            VerificacaoEstoqueScheduler scheduler =
                    new VerificacaoEstoqueScheduler(insumoRepository, whatsAppSender);

            Mockito.when(insumoRepository.findAll()).thenReturn(List.of(
                    insumo("Farinha de trigo", "INS-001", 2.0, 10.0, true),
                    insumo("Presunto", "INS-003", 12.0, 10.0, true),
                    insumo("Tomate inativo", "INS-006", 0.1, 5.0, false),
                    insumo("Orégano", "INS-004", 8.0, 8.0, true)
            ));

            Assertions.assertDoesNotThrow(scheduler::verificarEstoqueMinimo);

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(whatsAppSender, Mockito.times(1)).enviar(captor.capture());

            String mensagem = captor.getValue();
            Assertions.assertEquals(
                    "⚠️ Estoque baixo: Farinha de trigo (INS-001) | Atual: 2.0 | Mínimo: 10.0",
                    mensagem
            );
        }

        @Test
        @DisplayName("não deve despachar nenhuma notificação quando todos estão adequados")
        void naoDeveDespacharQuandoTodosAdequados() {
            VerificacaoEstoqueScheduler scheduler =
                    new VerificacaoEstoqueScheduler(insumoRepository, whatsAppSender);

            Mockito.when(insumoRepository.findAll()).thenReturn(List.of(
                    insumo("Presunto", "INS-003", 12.0, 10.0, true),
                    insumo("Orégano", "INS-004", 9.0, 8.0, true)
            ));

            Assertions.assertDoesNotThrow(scheduler::verificarEstoqueMinimo);

            Mockito.verifyNoInteractions(whatsAppSender);
        }

        @Test
        @DisplayName("não deve propagar erro quando o envio falha")
        void naoDevePropagarErroQuandoEnvioFalha() {
            VerificacaoEstoqueScheduler scheduler =
                    new VerificacaoEstoqueScheduler(insumoRepository, whatsAppSender);

            Mockito.when(insumoRepository.findAll()).thenReturn(List.of(
                    insumo("Farinha de trigo", "INS-001", 2.0, 10.0, true)
            ));
            Mockito.doThrow(new RuntimeException("falha simulada de envio"))
                    .when(whatsAppSender)
                    .enviar(Mockito.anyString());

            Assertions.assertDoesNotThrow(scheduler::verificarEstoqueMinimo);
        }
    }
}
