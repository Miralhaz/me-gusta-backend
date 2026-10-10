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
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.notificacao.AlertaEstoque;
import school.sptech.megusta.notificacao.AlertaEstoquePublisherPort;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe VerificacaoEstoqueScheduler")
class VerificacaoEstoqueSchedulerTest {

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AlertaEstoquePublisherPort alertaEstoquePublisher;

    private VerificacaoEstoqueScheduler scheduler() {
        return new VerificacaoEstoqueScheduler(insumoRepository, usuarioRepository, alertaEstoquePublisher);
    }

    private Insumo insumo(String nome, String codigo, Double qtdAtual, Double estoqueMinimo, boolean ativo) {
        Insumo insumo = new Insumo();
        insumo.setNome(nome);
        insumo.setCodigoInsumo(codigo);
        insumo.setQtdAtual(qtdAtual);
        insumo.setEstoqueMinimo(estoqueMinimo);
        insumo.setAtivo(ativo);
        return insumo;
    }

    private Usuario usuario(String telefone) {
        return new Usuario(null, "Usuário", "usuario@email.com", "senha", telefone);
    }

    @Nested
    @DisplayName("Método verificarEstoqueMinimo")
    class verificarEstoqueMinimo {

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("deve publicar apenas para o último usuário cadastrado (maior id) com os insumos ativos abaixo do mínimo")
        void devePublicarApenasParaOUltimoUsuario() {
            Mockito.when(insumoRepository.findAll()).thenReturn(List.of(
                    insumo("Farinha de trigo", "INS-001", 2.0, 10.0, true),
                    insumo("Presunto", "INS-003", 12.0, 10.0, true),
                    insumo("Tomate inativo", "INS-006", 0.1, 5.0, false),
                    insumo("Orégano", "INS-004", 8.0, 8.0, true)
            ));
            Mockito.when(usuarioRepository.findTopByOrderByIdDesc())
                    .thenReturn(Optional.of(usuario("+5511888888888")));

            Assertions.assertDoesNotThrow(scheduler()::verificarEstoqueMinimo);

            ArgumentCaptor<String> telefoneCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<List<AlertaEstoque>> alertasCaptor = ArgumentCaptor.forClass(List.class);

            Mockito.verify(alertaEstoquePublisher, Mockito.times(1))
                    .publicar(telefoneCaptor.capture(), alertasCaptor.capture());

            Assertions.assertEquals(List.of("+5511888888888"), telefoneCaptor.getAllValues());
            Assertions.assertEquals(
                    List.of(new AlertaEstoque("Farinha de trigo", "INS-001", 2.0, 10.0)),
                    alertasCaptor.getValue()
            );
        }

        @Test
        @DisplayName("não deve publicar nada quando nenhum insumo está abaixo do mínimo")
        void naoDevePublicarQuandoTodosAdequados() {
            Mockito.when(insumoRepository.findAll()).thenReturn(List.of(
                    insumo("Presunto", "INS-003", 12.0, 10.0, true),
                    insumo("Orégano", "INS-004", 9.0, 8.0, true)
            ));
            Mockito.when(usuarioRepository.findTopByOrderByIdDesc())
                    .thenReturn(Optional.of(usuario("+5511999999999")));

            Assertions.assertDoesNotThrow(scheduler()::verificarEstoqueMinimo);

            Mockito.verifyNoInteractions(alertaEstoquePublisher);
        }

        @Test
        @DisplayName("cenário sem usuários cadastrados: não deve publicar nada")
        void naoDevePublicarQuandoNaoHaUsuarios() {
            Mockito.when(insumoRepository.findAll()).thenReturn(List.of(
                    insumo("Farinha de trigo", "INS-001", 2.0, 10.0, true)
            ));
            Mockito.when(usuarioRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());

            Assertions.assertDoesNotThrow(scheduler()::verificarEstoqueMinimo);

            Mockito.verifyNoInteractions(alertaEstoquePublisher);
        }

        @Test
        @DisplayName("não deve propagar erro quando a publicação falha")
        void naoDevePropagarErroQuandoPublicacaoFalha() {
            Mockito.when(insumoRepository.findAll()).thenReturn(List.of(
                    insumo("Farinha de trigo", "INS-001", 2.0, 10.0, true)
            ));
            Mockito.when(usuarioRepository.findTopByOrderByIdDesc())
                    .thenReturn(Optional.of(usuario("+5511999999999")));
            Mockito.doThrow(new RuntimeException("falha simulada de publicação"))
                    .when(alertaEstoquePublisher)
                    .publicar(Mockito.anyString(), Mockito.anyList());

            Assertions.assertDoesNotThrow(scheduler()::verificarEstoqueMinimo);
        }
    }
}