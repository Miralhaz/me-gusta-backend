package school.sptech.megusta.notificacao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe NotificacaoEstoqueService")
class NotificacaoEstoqueServiceTest {

    @Mock
    private AlertaEstoquePublisherPort alertaEstoquePublisher;

    private NotificacaoEstoqueService notificacaoEstoqueService;

    @BeforeEach
    void setUp() {
        notificacaoEstoqueService = new NotificacaoEstoqueService(alertaEstoquePublisher);
    }

    private AlertaEstoque alerta(String nome, String codigo, Double qtdAtual, Double estoqueMinimo) {
        return new AlertaEstoque(nome, codigo, qtdAtual, estoqueMinimo);
    }

    @Nested
    @DisplayName("Fan-out por usuário")
    class fanOut {

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("deve publicar uma mensagem por usuário com a mesma lista de alertas")
        void devePublicarUmaMensagemPorUsuarioComAMesmaLista() {
            List<AlertaEstoque> alertas = List.of(
                    alerta("Farinha de trigo", "INS-001", 2.0, 10.0),
                    alerta("Queijo mussarela", "INS-002", 1.5, 4.0)
            );
            List<String> telefones = List.of("+5511999999999", "+5511888888888");

            int publicadas = notificacaoEstoqueService.notificarAbaixoMinimo(telefones, alertas);

            Assertions.assertEquals(2, publicadas);

            ArgumentCaptor<String> telefoneCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<List<AlertaEstoque>> alertasCaptor = ArgumentCaptor.forClass(List.class);

            Mockito.verify(alertaEstoquePublisher, Mockito.times(2))
                    .publicar(telefoneCaptor.capture(), alertasCaptor.capture());

            Assertions.assertEquals(telefones, telefoneCaptor.getAllValues());
            for (List<AlertaEstoque> listaPublicada : alertasCaptor.getAllValues()) {
                Assertions.assertEquals(alertas, listaPublicada);
            }
        }

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("deve publicar apenas os insumos abaixo do mínimo, em uma lista mista")
        void devePublicarApenasInsumosAbaixoDoMinimo() {
            List<AlertaEstoque> alertas = List.of(
                    alerta("Farinha de trigo", "INS-001", 2.0, 10.0),
                    alerta("Presunto", "INS-003", 12.0, 10.0),
                    alerta("Orégano", "INS-004", 8.0, 8.0),
                    alerta("Queijo mussarela", "INS-002", 1.5, 4.0)
            );

            notificacaoEstoqueService.notificarAbaixoMinimo(List.of("+5511999999999"), alertas);

            ArgumentCaptor<List<AlertaEstoque>> alertasCaptor = ArgumentCaptor.forClass(List.class);
            Mockito.verify(alertaEstoquePublisher).publicar(Mockito.anyString(), alertasCaptor.capture());

            Assertions.assertEquals(
                    List.of(
                            alerta("Farinha de trigo", "INS-001", 2.0, 10.0),
                            alerta("Queijo mussarela", "INS-002", 1.5, 4.0)
                    ),
                    alertasCaptor.getValue()
            );
        }
    }

    @Nested
    @DisplayName("Nenhuma publicação")
    class nenhumaPublicacao {

        @Test
        @DisplayName("não deve publicar quando nenhum insumo está abaixo do mínimo")
        void naoDevePublicarQuandoNenhumInsumoAbaixoDoMinimo() {
            List<AlertaEstoque> alertas = List.of(
                    alerta("Presunto", "INS-003", 12.0, 10.0),
                    alerta("Orégano", "INS-004", 8.0, 8.0)
            );

            int publicadas = notificacaoEstoqueService.notificarAbaixoMinimo(List.of("+5511999999999"), alertas);

            Assertions.assertEquals(0, publicadas);
            Mockito.verifyNoInteractions(alertaEstoquePublisher);
        }

        @Test
        @DisplayName("não deve publicar quando a lista de alertas está vazia")
        void naoDevePublicarQuandoListaDeAlertasVazia() {
            int publicadas = notificacaoEstoqueService.notificarAbaixoMinimo(List.of("+5511999999999"), List.of());

            Assertions.assertEquals(0, publicadas);
            Mockito.verifyNoInteractions(alertaEstoquePublisher);
        }

        @Test
        @DisplayName("não deve publicar quando não há usuários/telefones cadastrados")
        void naoDevePublicarQuandoNaoHaTelefones() {
            List<AlertaEstoque> alertas = List.of(alerta("Farinha de trigo", "INS-001", 2.0, 10.0));

            int publicadas = notificacaoEstoqueService.notificarAbaixoMinimo(List.of(), alertas);

            Assertions.assertEquals(0, publicadas);
            Mockito.verifyNoInteractions(alertaEstoquePublisher);
        }

        @Test
        @DisplayName("deve ignorar telefone nulo ou em branco")
        void deveIgnorarTelefoneNuloOuEmBranco() {
            List<AlertaEstoque> alertas = List.of(alerta("Farinha de trigo", "INS-001", 2.0, 10.0));

            int publicadas = notificacaoEstoqueService.notificarAbaixoMinimo(
                    java.util.Arrays.asList(null, "  ", "+5511999999999"),
                    alertas
            );

            Assertions.assertEquals(1, publicadas);
            Mockito.verify(alertaEstoquePublisher, Mockito.times(1))
                    .publicar(Mockito.eq("+5511999999999"), Mockito.anyList());
        }
    }

    @Nested
    @DisplayName("Isolamento de falhas")
    class isolamentoDeFalhas {

        @Test
        @DisplayName("falha em um destinatário não interrompe os demais nem propaga erro")
        void falhaEmUmDestinatarioNaoInterrompeOsDemais() {
            List<AlertaEstoque> alertas = List.of(alerta("Farinha de trigo", "INS-001", 2.0, 10.0));
            List<String> telefones = List.of("+5511999999999", "+5511888888888", "+5511777777777");

            Mockito.doThrow(new RuntimeException("falha simulada de publicação"))
                    .when(alertaEstoquePublisher)
                    .publicar(Mockito.eq("+5511999999999"), Mockito.anyList());

            int publicadas = Assertions.assertDoesNotThrow(() ->
                    notificacaoEstoqueService.notificarAbaixoMinimo(telefones, alertas));

            Assertions.assertEquals(2, publicadas);
            Mockito.verify(alertaEstoquePublisher, Mockito.times(3))
                    .publicar(Mockito.anyString(), Mockito.anyList());
        }
    }
}
