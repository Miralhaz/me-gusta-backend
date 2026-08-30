package school.sptech.megusta.notificacao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe NotificacaoEstoqueService")
class NotificacaoEstoqueServiceTest {

    @Mock
    private WhatsAppSenderPort whatsAppSender;

    @InjectMocks
    private NotificacaoEstoqueService notificacaoEstoqueService;

    private AlertaEstoque alerta(String nome, String codigo, Double qtdAtual, Double estoqueMinimo) {
        return new AlertaEstoque(nome, codigo, qtdAtual, estoqueMinimo);
    }

    @Nested
    @DisplayName("Método notificarAbaixoMinimo")
    class notificarAbaixoMinimo {

        @Test
        @DisplayName("deve enviar uma mensagem por insumo elegível")
        void deveEnviarUmaMensagemPorInsumoElegivel() {
            List<AlertaEstoque> alertas = List.of(
                    alerta("Farinha de trigo", "INS-001", 2.0, 10.0),
                    alerta("Queijo mussarela", "INS-002", 1.5, 4.0)
            );

            int enviados = notificacaoEstoqueService.notificarAbaixoMinimo(alertas);

            Assertions.assertEquals(2, enviados);
            Mockito.verify(whatsAppSender, Mockito.times(2)).enviar(Mockito.anyString());
        }

        @Test
        @DisplayName("deve conter nome, código, quantidade atual e mínimo na mensagem")
        void deveConterDadosCompletosNaMensagem() {
            List<AlertaEstoque> alertas = List.of(
                    alerta("Farinha de trigo", "INS-001", 2.0, 10.0)
            );

            notificacaoEstoqueService.notificarAbaixoMinimo(alertas);

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(whatsAppSender, Mockito.times(1)).enviar(captor.capture());

            String mensagem = captor.getValue();
            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            "⚠️ Estoque baixo: Farinha de trigo (INS-001) | Atual: 2.0 | Mínimo: 10.0",
                            mensagem
                    ),
                    () -> Assertions.assertTrue(mensagem.contains("Farinha de trigo")),
                    () -> Assertions.assertTrue(mensagem.contains("INS-001")),
                    () -> Assertions.assertTrue(mensagem.contains("2.0")),
                    () -> Assertions.assertTrue(mensagem.contains("10.0"))
            );
        }

        @Test
        @DisplayName("não deve enviar para estoque adequado (maior ou igual ao mínimo)")
        void naoDeveEnviarParaEstoqueAdequado() {
            List<AlertaEstoque> alertas = List.of(
                    alerta("Presunto", "INS-003", 12.0, 10.0),
                    alerta("Orégano", "INS-004", 8.0, 8.0)
            );

            int enviados = notificacaoEstoqueService.notificarAbaixoMinimo(alertas);

            Assertions.assertEquals(0, enviados);
            Mockito.verifyNoInteractions(whatsAppSender);
        }

        @Test
        @DisplayName("deve enviar apenas os elegíveis em uma lista mista")
        void deveEnviarApenasElegiveisEmListaMista() {
            List<AlertaEstoque> alertas = List.of(
                    alerta("Farinha de trigo", "INS-001", 2.0, 10.0),
                    alerta("Presunto", "INS-003", 12.0, 10.0),
                    alerta("Queijo mussarela", "INS-002", 1.5, 4.0)
            );

            int enviados = notificacaoEstoqueService.notificarAbaixoMinimo(alertas);

            Assertions.assertEquals(2, enviados);

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(whatsAppSender, Mockito.times(2)).enviar(captor.capture());

            Assertions.assertEquals(
                    List.of(
                            "⚠️ Estoque baixo: Farinha de trigo (INS-001) | Atual: 2.0 | Mínimo: 10.0",
                            "⚠️ Estoque baixo: Queijo mussarela (INS-002) | Atual: 1.5 | Mínimo: 4.0"
                    ),
                    captor.getAllValues()
            );
        }

        @Test
        @DisplayName("não deve enviar nada para lista vazia")
        void naoDeveEnviarNadaParaListaVazia() {
            int enviados = notificacaoEstoqueService.notificarAbaixoMinimo(List.of());

            Assertions.assertEquals(0, enviados);
            Mockito.verifyNoInteractions(whatsAppSender);
        }
    }

    @Nested
    @DisplayName("Resiliência de envio")
    class resiliencia {

        @Test
        @DisplayName("falha em um insumo não interrompe os demais nem propaga erro")
        void falhaEmUmInsumoNaoInterrompeOsDemais() {
            List<AlertaEstoque> alertas = List.of(
                    alerta("Farinha de trigo", "INS-001", 2.0, 10.0),
                    alerta("Queijo mussarela", "INS-002", 1.5, 4.0),
                    alerta("Calabresa", "INS-005", 0.5, 3.0)
            );

            Mockito.doThrow(new RuntimeException("falha simulada de envio"))
                    .when(whatsAppSender)
                    .enviar(Mockito.contains("Farinha"));

            Assertions.assertDoesNotThrow(() ->
                    notificacaoEstoqueService.notificarAbaixoMinimo(alertas));

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(whatsAppSender, Mockito.times(3)).enviar(captor.capture());

            Assertions.assertEquals(
                    List.of(
                            "⚠️ Estoque baixo: Farinha de trigo (INS-001) | Atual: 2.0 | Mínimo: 10.0",
                            "⚠️ Estoque baixo: Queijo mussarela (INS-002) | Atual: 1.5 | Mínimo: 4.0",
                            "⚠️ Estoque baixo: Calabresa (INS-005) | Atual: 0.5 | Mínimo: 3.0"
                    ),
                    captor.getAllValues()
            );
        }
    }
}
