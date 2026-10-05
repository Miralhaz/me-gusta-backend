package school.sptech.megusta.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import school.sptech.megusta.dto.planilha_vendas.BaixaInsumoResponse;
import school.sptech.megusta.dto.planilha_vendas.ItemVendido;
import school.sptech.megusta.exception.AcessoNegadoException;
import school.sptech.megusta.exception.EstoqueInsuficienteException;
import school.sptech.megusta.exception.PlanilhaInvalidaException;
import school.sptech.megusta.model.FogazzaInsumo;
import school.sptech.megusta.model.Fogazzas;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.Motivo;
import school.sptech.megusta.model.SaidaEstoque;
import school.sptech.megusta.model.TipoStatus;
import school.sptech.megusta.model.UnidadeMedida;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.repository.FogazzaInsumoRepository;
import school.sptech.megusta.repository.FogazzasRepository;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.MotivoRepository;
import school.sptech.megusta.repository.SaidaEstoqueRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de VendasService")
class VendasServiceTest {

    @Mock
    private FogazzasRepository fogazzasRepository;

    @Mock
    private FogazzaInsumoRepository fogazzaInsumoRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private MotivoRepository motivoRepository;

    @Mock
    private SaidaEstoqueRepository saidaEstoqueRepository;

    @Mock
    private TipoStatusService tipoStatusService;

    @Spy
    private PlanilhaVendasExtractor planilhaVendasExtractor = new PlanilhaVendasExtractor();

    @InjectMocks
    private VendasService vendasService;

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    // ---------------------------------------------------------------
    // Leitura do relatório de itens vendidos
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve extrair os itens vendidos do relatório de itens vendidos")
    void deveExtrairItensDoRelatorioDeItensVendidos() throws IOException {
        List<ItemVendido> itens = vendasService.lerPlanilha(
                arquivo("/historico_itens_vendidos.xlsx", "historico.xlsx"));

        Assertions.assertEquals(1, itens.size());
        Assertions.assertEquals("Fogazza Palmito com Catupiry", itens.get(0).getNomeItem());
        Assertions.assertEquals(BigDecimal.valueOf(1.0), itens.get(0).getQuantidade());
    }

    @Test
    @DisplayName("Deve ignorar linhas sem nome, com quantidade não numérica, zero ou negativa")
    void deveIgnorarLinhasInvalidas() throws IOException {
        List<ItemVendido> itens = vendasService.lerPlanilha(planilhaComLinhasInvalidas());

        Assertions.assertEquals(1, itens.size());
        Assertions.assertEquals("Fogazza de Mussarela", itens.get(0).getNomeItem());
        Assertions.assertEquals(BigDecimal.valueOf(2.0), itens.get(0).getQuantidade());
    }

    @Test
    @DisplayName("Deve lançar PlanilhaInvalidaException para planilha sem a assinatura Nome Prod + Qtd.")
    void deveLancarPlanilhaInvalidaParaLayoutNaoSuportado() throws IOException {
        MockMultipartFile planilha = arquivoPlanilha("produtos.xlsx",
                new String[]{"Produto", "Valor Total"},
                new Object[][]{{"Fogazza de Mussarela", 65.80}});

        PlanilhaInvalidaException excecao = Assertions.assertThrows(PlanilhaInvalidaException.class,
                () -> vendasService.lerPlanilha(planilha));

        Assertions.assertTrue(excecao.getMessage().contains("produtos.xlsx"));
        Assertions.assertTrue(excecao.getMessage().contains("Nome Prod"));
        Assertions.assertTrue(excecao.getMessage().contains("Qtd."));
    }

    @Test
    @DisplayName("Deve lançar PlanilhaInvalidaException para a planilha de Pedidos Recentes")
    void deveLancarPlanilhaInvalidaParaPedidosRecentes() throws IOException {
        MockMultipartFile planilha = arquivoPlanilha("pedidos.xlsx",
                new String[]{"Número do pedido", "Status do pedido", "Itens", "Ganhos"},
                new Object[][]{
                        {"6406", "Concluído", "Fogazza de Mussarela;Fogazza de Portuguesa;", 65.28},
                        {"6407", "Concluído", "Fogazza de Mussarela", 32.90}
                });

        Assertions.assertThrows(PlanilhaInvalidaException.class, () -> vendasService.lerPlanilha(planilha));
    }

    @Test
    @DisplayName("Deve lançar PlanilhaInvalidaException quando a planilha tem Nome Prod mas não Qtd.")
    void deveLancarPlanilhaInvalidaQuandoFaltaColunaQuantidade() throws IOException {
        MockMultipartFile planilha = arquivoPlanilha("nome_sem_qtd.xlsx",
                new String[]{"Itens", "Nome Prod"},
                new Object[][]{{"Fogazza de Mussarela;Fogazza de Portuguesa;", "Pedido 6406"}});

        Assertions.assertThrows(PlanilhaInvalidaException.class, () -> vendasService.lerPlanilha(planilha));
    }

    @Test
    @DisplayName("Deve rejeitar a planilha não reconhecida sem chamar nenhum repository de escrita")
    void deveRejeitarPlanilhaNaoReconhecidaSemEscreverNoBanco() throws IOException {
        autenticar();
        MockMultipartFile planilha = arquivoPlanilha("produtos.xlsx",
                new String[]{"Produto", "Valor Total"},
                new Object[][]{{"Fogazza de Mussarela", 65.80}});

        Assertions.assertThrows(PlanilhaInvalidaException.class,
                () -> vendasService.importarPlanilha(planilha));

        verify(insumoRepository, never()).save(any(Insumo.class));
        verify(saidaEstoqueRepository, never()).save(any(SaidaEstoque.class));
        verify(motivoRepository, never()).save(any(Motivo.class));
    }

    // ---------------------------------------------------------------
    // Orquestração: consultas, acumulação, persistência e resposta por insumo
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve consultar a fogazza e os insumos e persistir insumo e saída com os valores acumulados")
    void deveOrquestrarBaixaDeEstoqueEInvocarAsConsultasCorretas() {
        autenticar();
        ItemVendido item = new ItemVendido("Fogazza de Mussarela", BigDecimal.valueOf(2));

        Fogazzas fogazza = new Fogazzas();
        fogazza.setId(1);
        fogazza.setNome("Fogazza de Mussarela");

        Insumo insumo10 = insumo(10, 100.0);
        Insumo insumo11 = insumo(11, 50.0);
        FogazzaInsumo registro10 = registro(insumo10, "0.5");
        FogazzaInsumo registro11 = registro(insumo11, "1.5");

        Motivo motivo = new Motivo();
        motivo.setId(5);
        motivo.setNome("Venda");

        TipoStatus status = new TipoStatus();
        status.setId(3);
        status.setNome("CRITICO");

        when(fogazzasRepository.findByNomeIgnoreCase("Fogazza de Mussarela")).thenReturn(Optional.of(fogazza));
        when(fogazzaInsumoRepository.findByFogazzaId(1)).thenReturn(List.of(registro10, registro11));
        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.of(motivo));
        when(insumoRepository.findById(10)).thenReturn(Optional.of(insumo10));
        when(insumoRepository.findById(11)).thenReturn(Optional.of(insumo11));
        when(tipoStatusService.calcularStatusEstoque(any(), any())).thenReturn(status);

        List<BaixaInsumoResponse> baixas = vendasService.baixarEstoque(List.of(item));

        verify(fogazzasRepository).findByNomeIgnoreCase("Fogazza de Mussarela");
        verify(fogazzaInsumoRepository).findByFogazzaId(1);
        verify(tipoStatusService, times(2)).calcularStatusEstoque(any(), any());

        Assertions.assertEquals(99.0, insumo10.getQtdAtual());
        Assertions.assertEquals(47.0, insumo11.getQtdAtual());
        Assertions.assertEquals(status, insumo10.getTipoStatus());
        Assertions.assertEquals(status, insumo11.getTipoStatus());

        verify(insumoRepository).save(insumo10);
        verify(insumoRepository).save(insumo11);

        ArgumentCaptor<SaidaEstoque> captor = ArgumentCaptor.forClass(SaidaEstoque.class);
        verify(saidaEstoqueRepository, times(2)).save(captor.capture());

        List<SaidaEstoque> saidas = captor.getAllValues();
        Assertions.assertEquals(2, saidas.size());
        SaidaEstoque saidaInsumo10 = saidas.stream()
                .filter(s -> s.getInsumo().getId() == 10).findFirst().orElseThrow();
        SaidaEstoque saidaInsumo11 = saidas.stream()
                .filter(s -> s.getInsumo().getId() == 11).findFirst().orElseThrow();

        Assertions.assertEquals(new BigDecimal("1.0"), saidaInsumo10.getQuantidade());
        Assertions.assertEquals(new BigDecimal("3.0"), saidaInsumo11.getQuantidade());
        Assertions.assertEquals(motivo, saidaInsumo10.getMotivo());
        Assertions.assertSame(usuarioAtivo, saidaInsumo10.getUsuario());
        Assertions.assertEquals(motivo, saidaInsumo11.getMotivo());
        Assertions.assertSame(usuarioAtivo, saidaInsumo11.getUsuario());

        Assertions.assertEquals(2, baixas.size());
    }

    @Test
    @DisplayName("Deve descrever a baixa de cada insumo com a quantidade antes, subtraída e depois")
    void deveDescreverBaixaDeCadaInsumoComQuantidadesAntesSubtraidaEDepois() {
        autenticar();
        Motivo motivo = new Motivo();
        motivo.setId(5);
        motivo.setNome("Venda");

        Fogazzas fogazza = new Fogazzas();
        fogazza.setId(1);
        fogazza.setNome("Fogazza de Mussarela");

        Insumo insumo = insumo(10, 100.0);
        insumo.setNome("Farinha de Trigo");
        insumo.setCodigoInsumo("FT-001");
        FogazzaInsumo registro = registro(insumo, "3");

        // 4 fogazzas × 3 de insumo = 12 consumidos de um estoque de 100
        ItemVendido item = new ItemVendido("Fogazza de Mussarela", BigDecimal.valueOf(4));

        when(fogazzasRepository.findByNomeIgnoreCase("Fogazza de Mussarela")).thenReturn(Optional.of(fogazza));
        when(fogazzaInsumoRepository.findByFogazzaId(1)).thenReturn(List.of(registro));
        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.of(motivo));
        when(insumoRepository.findById(10)).thenReturn(Optional.of(insumo));
        when(tipoStatusService.calcularStatusEstoque(any(), any())).thenReturn(new TipoStatus());

        List<BaixaInsumoResponse> baixas = vendasService.baixarEstoque(List.of(item));

        Assertions.assertEquals(1, baixas.size());
        BaixaInsumoResponse baixa = baixas.get(0);
        Assertions.assertEquals("Farinha de Trigo", baixa.getNomeInsumo());
        Assertions.assertEquals("FT-001", baixa.getCodigoInsumo());
        Assertions.assertEquals("kg", baixa.getUnidadeMedida());
        Assertions.assertEquals(new BigDecimal("100.0"), baixa.getQuantidadeAtual());
        Assertions.assertEquals(new BigDecimal("12"), baixa.getQuantidadeSubtraida());
        Assertions.assertEquals(new BigDecimal("88.0"), baixa.getQuantidadeAposSubtracao());
        Assertions.assertEquals(88.0, insumo.getQtdAtual());
    }

    @Test
    @DisplayName("Deve devolver um único registro do insumo consumido por duas fogazzas diferentes")
    void deveDevolverRegistroUnicoParaInsumoConsumidoPorDuasFogazzas() {
        autenticar();
        Motivo motivo = new Motivo();
        motivo.setId(5);
        motivo.setNome("Venda");

        Fogazzas mussarela = new Fogazzas();
        mussarela.setId(1);
        mussarela.setNome("Fogazza de Mussarela");

        Fogazzas portuguesa = new Fogazzas();
        portuguesa.setId(2);
        portuguesa.setNome("Fogazza de Portuguesa");

        Insumo insumo = insumo(10, 100.0);
        insumo.setNome("Farinha de Trigo");
        insumo.setCodigoInsumo("FT-001");
        FogazzaInsumo receitaMussarela = registro(insumo, "2");
        FogazzaInsumo receitaPortuguesa = registro(insumo, "3");

        ItemVendido item1 = new ItemVendido("Fogazza de Mussarela", BigDecimal.valueOf(3));
        ItemVendido item2 = new ItemVendido("Fogazza de Portuguesa", BigDecimal.valueOf(2));

        when(fogazzasRepository.findByNomeIgnoreCase("Fogazza de Mussarela")).thenReturn(Optional.of(mussarela));
        when(fogazzasRepository.findByNomeIgnoreCase("Fogazza de Portuguesa")).thenReturn(Optional.of(portuguesa));
        when(fogazzaInsumoRepository.findByFogazzaId(1)).thenReturn(List.of(receitaMussarela));
        when(fogazzaInsumoRepository.findByFogazzaId(2)).thenReturn(List.of(receitaPortuguesa));
        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.of(motivo));
        when(insumoRepository.findById(10)).thenReturn(Optional.of(insumo));
        when(tipoStatusService.calcularStatusEstoque(any(), any())).thenReturn(new TipoStatus());

        List<BaixaInsumoResponse> baixas = vendasService.baixarEstoque(List.of(item1, item2));

        // 3 × 2 + 2 × 3 = 12 consumidos, em um único registro
        Assertions.assertEquals(1, baixas.size());
        Assertions.assertEquals(new BigDecimal("12"), baixas.get(0).getQuantidadeSubtraida());
        Assertions.assertEquals(new BigDecimal("100.0"), baixas.get(0).getQuantidadeAtual());
        Assertions.assertEquals(new BigDecimal("88.0"), baixas.get(0).getQuantidadeAposSubtracao());
        Assertions.assertEquals(88.0, insumo.getQtdAtual());

        verify(insumoRepository, times(1)).save(insumo);
        verify(saidaEstoqueRepository, times(1)).save(any(SaidaEstoque.class));
    }

    @Test
    @DisplayName("importarPlanilha deve devolver os insumos alterados, e não os itens lidos")
    void deveDevolverOsInsumosAlteradosENaoOsItensLidos() throws IOException {
        autenticar();
        Motivo motivo = new Motivo();
        motivo.setId(5);
        motivo.setNome("Venda");

        Fogazzas fogazza = new Fogazzas();
        fogazza.setId(1);
        fogazza.setNome("Fogazza de Mussarela");

        Insumo insumo = insumo(10, 50.0);
        insumo.setNome("Farinha de Trigo");
        insumo.setCodigoInsumo("FT-001");
        FogazzaInsumo registro = registro(insumo, "2");

        when(fogazzasRepository.findByNomeIgnoreCase("Fogazza de Mussarela")).thenReturn(Optional.of(fogazza));
        when(fogazzaInsumoRepository.findByFogazzaId(1)).thenReturn(List.of(registro));
        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.of(motivo));
        when(insumoRepository.findById(10)).thenReturn(Optional.of(insumo));
        when(tipoStatusService.calcularStatusEstoque(any(), any())).thenReturn(new TipoStatus());

        // Três itens vendidos, mas só um tem fogazza cadastrada
        MockMultipartFile planilha = arquivoPlanilha("relatorio.xlsx",
                new String[]{"Data/Hora Item", "Qtd.", "Nome Prod"},
                new Object[][]{
                        {"01/07/2026 19:32", 5, "Fogazza de Mussarela"},
                        {"01/07/2026 19:40", 3, "Coca-Cola Zero 350ml"},
                        {"01/07/2026 19:50", 2, "Promoção que não existe"}
                });

        List<BaixaInsumoResponse> baixas = vendasService.importarPlanilha(planilha);

        // A resposta traz o insumo alterado (5 × 2 = 10), não os 3 itens lidos
        Assertions.assertEquals(1, baixas.size());
        Assertions.assertEquals("Farinha de Trigo", baixas.get(0).getNomeInsumo());
        Assertions.assertEquals(new BigDecimal("10.0"), baixas.get(0).getQuantidadeSubtraida());
        Assertions.assertEquals(new BigDecimal("50.0"), baixas.get(0).getQuantidadeAtual());
        Assertions.assertEquals(new BigDecimal("40.0"), baixas.get(0).getQuantidadeAposSubtracao());
    }

    // ---------------------------------------------------------------
    // Resolução do usuário autenticado
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve lançar AcessoNegadoException quando não há usuário autenticado")
    void deveLancarExcecaoQuandoNaoHaUsuarioAutenticado() {
        ItemVendido item = new ItemVendido("Fogazza de Mussarela", BigDecimal.ONE);

        Assertions.assertThrows(AcessoNegadoException.class,
                () -> vendasService.baixarEstoque(List.of(item)));
    }

    @Test
    @DisplayName("Deve lançar AcessoNegadoException quando o principal não é um Usuario")
    void deveLancarExcecaoQuandoPrincipalNaoEhUsuario() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", null));

        ItemVendido item = new ItemVendido("Fogazza de Mussarela", BigDecimal.ONE);

        Assertions.assertThrows(AcessoNegadoException.class,
                () -> vendasService.baixarEstoque(List.of(item)));
    }

    // ---------------------------------------------------------------
    // Resolução do motivo "Venda"
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve reutilizar o motivo Venda quando já cadastrado")
    void deveReutilizarMotivoVendaExistente() {
        autenticar();
        Motivo motivo = new Motivo();
        motivo.setId(5);
        motivo.setNome("Venda");

        ItemVendido item = itemComReceita("Fogazza de Mussarela");
        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.of(motivo));
        when(tipoStatusService.calcularStatusEstoque(any(), any())).thenReturn(new TipoStatus());

        vendasService.baixarEstoque(List.of(item));

        verify(motivoRepository).findByNomeIgnoreCase("Venda");
        verify(motivoRepository, never()).save(any(Motivo.class));

        ArgumentCaptor<SaidaEstoque> captor = ArgumentCaptor.forClass(SaidaEstoque.class);
        verify(saidaEstoqueRepository).save(captor.capture());
        Assertions.assertEquals(motivo, captor.getValue().getMotivo());
    }

    @Test
    @DisplayName("Deve criar e salvar o motivo Venda quando inexistente")
    void deveCriarMotivoVendaQuandoInexistente() {
        autenticar();

        ItemVendido item = itemComReceita("Fogazza de Mussarela");

        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.empty());
        when(tipoStatusService.calcularStatusEstoque(any(), any())).thenReturn(new TipoStatus());
        Motivo motivoCriado = new Motivo();
        motivoCriado.setNome("Venda");
        when(motivoRepository.save(any(Motivo.class))).thenReturn(motivoCriado);

        vendasService.baixarEstoque(List.of(item));

        ArgumentCaptor<Motivo> captorMotivo = ArgumentCaptor.forClass(Motivo.class);
        verify(motivoRepository).save(captorMotivo.capture());
        Assertions.assertEquals("Venda", captorMotivo.getValue().getNome());

        ArgumentCaptor<SaidaEstoque> captorSaida = ArgumentCaptor.forClass(SaidaEstoque.class);
        verify(saidaEstoqueRepository).save(captorSaida.capture());
        Assertions.assertEquals(motivoCriado, captorSaida.getValue().getMotivo());
    }

    // ---------------------------------------------------------------
    // Itens sem fogazza cadastrada e fogazza sem insumos
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve ignorar itens sem fogazza cadastrada e continuar processando os demais")
    void deveIgnorarItensSemFogazzaCadastrada() {
        autenticar();
        Motivo motivo = new Motivo();
        motivo.setId(5);
        motivo.setNome("Venda");

        Fogazzas fogazza = new Fogazzas();
        fogazza.setId(1);
        fogazza.setNome("Fogazza de Mussarela");

        Insumo insumo = insumo(10, 100.0);
        FogazzaInsumo registro = registro(insumo, "2");

        ItemVendido encontrada = new ItemVendido("Fogazza de Mussarela", BigDecimal.ONE);
        ItemVendido naoEncontrada = new ItemVendido("Refrigerante Coca-Cola 350ml", BigDecimal.valueOf(5));

        when(fogazzasRepository.findByNomeIgnoreCase("Fogazza de Mussarela")).thenReturn(Optional.of(fogazza));
        when(fogazzasRepository.findByNomeIgnoreCase("Refrigerante Coca-Cola 350ml")).thenReturn(Optional.empty());
        when(fogazzaInsumoRepository.findByFogazzaId(1)).thenReturn(List.of(registro));
        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.of(motivo));
        when(insumoRepository.findById(10)).thenReturn(Optional.of(insumo));
        when(tipoStatusService.calcularStatusEstoque(any(), any())).thenReturn(new TipoStatus());

        List<BaixaInsumoResponse> baixas =
                Assertions.assertDoesNotThrow(() -> vendasService.baixarEstoque(List.of(encontrada, naoEncontrada)));

        verify(fogazzasRepository).findByNomeIgnoreCase("Fogazza de Mussarela");
        verify(fogazzasRepository).findByNomeIgnoreCase("Refrigerante Coca-Cola 350ml");
        verify(insumoRepository).save(insumo);
        Assertions.assertEquals(98.0, insumo.getQtdAtual());
        Assertions.assertEquals(1, baixas.size());
    }

    @Test
    @DisplayName("Deve devolver lista vazia quando nenhum nome corresponde a uma fogazza cadastrada")
    void deveDevolverListaVaziaQuandoNenhumNomeCorrespondeAFogazza() {
        autenticar();
        Motivo motivo = new Motivo();
        motivo.setId(5);
        motivo.setNome("Venda");

        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.of(motivo));
        when(fogazzasRepository.findByNomeIgnoreCase(any())).thenReturn(Optional.empty());

        List<BaixaInsumoResponse> baixas = vendasService.baixarEstoque(List.of(
                new ItemVendido("Compre 3 Fogazzas - Ganhe refri 350ml", BigDecimal.valueOf(5)),
                new ItemVendido("Promoção inexistente", BigDecimal.ONE)));

        Assertions.assertTrue(baixas.isEmpty());
        verify(insumoRepository, never()).save(any(Insumo.class));
        verify(saidaEstoqueRepository, never()).save(any(SaidaEstoque.class));
    }

    @Test
    @DisplayName("Deve ignorar fogazza sem insumos vinculados sem gerar saída")
    void deveIgnorarFogazzaSemInsumosVinculados() {
        autenticar();
        Motivo motivo = new Motivo();
        motivo.setId(5);
        motivo.setNome("Venda");

        Fogazzas fogazza = new Fogazzas();
        fogazza.setId(1);
        fogazza.setNome("Fogazza de Mussarela");

        when(fogazzasRepository.findByNomeIgnoreCase("Fogazza de Mussarela")).thenReturn(Optional.of(fogazza));
        when(fogazzaInsumoRepository.findByFogazzaId(1)).thenReturn(List.of());
        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.of(motivo));

        List<BaixaInsumoResponse> baixas =
                vendasService.baixarEstoque(List.of(new ItemVendido("Fogazza de Mussarela", BigDecimal.ONE)));

        Assertions.assertTrue(baixas.isEmpty());
        verify(insumoRepository, never()).save(any(Insumo.class));
        verify(saidaEstoqueRepository, never()).save(any(SaidaEstoque.class));
    }

    // ---------------------------------------------------------------
    // Comportamento atômico (rollback)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve propagar a falha sem persistir insumo nem saída quando a baixa falha")
    void deveGarantirComportamentoAtomicoEmFalha() {
        autenticar();
        Motivo motivo = new Motivo();
        motivo.setId(5);
        motivo.setNome("Venda");

        ItemVendido item = itemComReceita("Fogazza de Mussarela");

        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.of(motivo));
        when(tipoStatusService.calcularStatusEstoque(any(), any()))
                .thenThrow(new IllegalStateException("falha ao calcular status"));

        Assertions.assertThrows(IllegalStateException.class,
                () -> vendasService.baixarEstoque(List.of(item)));

        verify(insumoRepository, never()).save(any(Insumo.class));
        verify(saidaEstoqueRepository, never()).save(any(SaidaEstoque.class));
    }

    // ---------------------------------------------------------------
    // Estoque insuficiente (qtdAtual < consumo da importação)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve lançar EstoqueInsuficienteException quando o consumo excede o estoque atual")
    void deveLancarEstoqueInsuficienteQuandoConsumoExcedeEstoque() {
        autenticar();
        Motivo motivo = new Motivo();
        motivo.setId(5);
        motivo.setNome("Venda");

        Fogazzas fogazza = new Fogazzas();
        fogazza.setId(1);
        fogazza.setNome("Fogazza de Mussarela");

        Insumo insumo = insumo(10, 10.0);
        insumo.setNome("Farinha de Trigo");
        insumo.setCodigoInsumo("FT-001");
        FogazzaInsumo registro = registro(insumo, "3");

        // 4 fogazzas × 3 de insumo = 12, mas o estoque é 10
        ItemVendido item = new ItemVendido("Fogazza de Mussarela", BigDecimal.valueOf(4));

        when(fogazzasRepository.findByNomeIgnoreCase("Fogazza de Mussarela")).thenReturn(Optional.of(fogazza));
        when(fogazzaInsumoRepository.findByFogazzaId(1)).thenReturn(List.of(registro));
        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.of(motivo));
        when(insumoRepository.findById(10)).thenReturn(Optional.of(insumo));

        EstoqueInsuficienteException excecao = Assertions.assertThrows(EstoqueInsuficienteException.class,
                () -> vendasService.baixarEstoque(List.of(item)));

        Assertions.assertTrue(excecao.getMessage().contains("Farinha de Trigo"));
        Assertions.assertTrue(excecao.getMessage().contains("Disponível"));
        Assertions.assertTrue(excecao.getMessage().contains("12.0"));

        verify(insumoRepository, never()).save(any(Insumo.class));
        verify(saidaEstoqueRepository, never()).save(any(SaidaEstoque.class));
    }

    @Test
    @DisplayName("Deve lançar EstoqueInsuficienteException quando o estoque atual é menor que o necessário")
    void deveLancarEstoqueInsuficienteQuandoEstoqueMenorQueNecessario() {
        autenticar();
        Motivo motivo = new Motivo();
        motivo.setId(5);
        motivo.setNome("Venda");

        Fogazzas fogazza = new Fogazzas();
        fogazza.setId(1);
        fogazza.setNome("Fogazza de Mussarela");

        Insumo insumo = insumo(10, 2.0);
        insumo.setNome("Farinha de Trigo");
        insumo.setCodigoInsumo("FT-001");
        FogazzaInsumo registro = registro(insumo, "5");

        // 1 fogazza × 5 de insumo = 5, mas o estoque é 2
        ItemVendido item = new ItemVendido("Fogazza de Mussarela", BigDecimal.ONE);

        when(fogazzasRepository.findByNomeIgnoreCase("Fogazza de Mussarela")).thenReturn(Optional.of(fogazza));
        when(fogazzaInsumoRepository.findByFogazzaId(1)).thenReturn(List.of(registro));
        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.of(motivo));
        when(insumoRepository.findById(10)).thenReturn(Optional.of(insumo));

        Assertions.assertThrows(EstoqueInsuficienteException.class,
                () -> vendasService.baixarEstoque(List.of(item)));

        verify(insumoRepository, never()).save(any(Insumo.class));
        verify(saidaEstoqueRepository, never()).save(any(SaidaEstoque.class));
    }

    @Test
    @DisplayName("Deve permitir baixa que zera o estoque do insumo sem lançar exceção")
    void devePermitirBaixaQueZeraEstoque() {
        autenticar();
        Motivo motivo = new Motivo();
        motivo.setId(5);
        motivo.setNome("Venda");

        Fogazzas fogazza = new Fogazzas();
        fogazza.setId(1);
        fogazza.setNome("Fogazza de Mussarela");

        Insumo insumo = insumo(10, 12.0);
        FogazzaInsumo registro = registro(insumo, "3");

        // 4 fogazzas × 3 de insumo = 12 → estoque zera (estado válido)
        ItemVendido item = new ItemVendido("Fogazza de Mussarela", BigDecimal.valueOf(4));

        when(fogazzasRepository.findByNomeIgnoreCase("Fogazza de Mussarela")).thenReturn(Optional.of(fogazza));
        when(fogazzaInsumoRepository.findByFogazzaId(1)).thenReturn(List.of(registro));
        when(motivoRepository.findByNomeIgnoreCase("Venda")).thenReturn(Optional.of(motivo));
        when(insumoRepository.findById(10)).thenReturn(Optional.of(insumo));
        when(tipoStatusService.calcularStatusEstoque(any(), any())).thenReturn(new TipoStatus());

        List<BaixaInsumoResponse> baixas =
                Assertions.assertDoesNotThrow(() -> vendasService.baixarEstoque(List.of(item)));

        Assertions.assertEquals(0.0, insumo.getQtdAtual());
        Assertions.assertEquals(new BigDecimal("12.0"), baixas.get(0).getQuantidadeAtual());
        Assertions.assertEquals(new BigDecimal("0.0"), baixas.get(0).getQuantidadeAposSubtracao());
        verify(insumoRepository).save(insumo);
        verify(saidaEstoqueRepository).save(any(SaidaEstoque.class));
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Usuario usuarioAtivo;

    private void autenticar() {
        usuarioAtivo = new Usuario(1, "João", "joao@email.com", "senha");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuarioAtivo, null, usuarioAtivo.getAuthorities()));
    }

    private Insumo insumo(Integer id, Double qtdAtual) {
        UnidadeMedida unidade = new UnidadeMedida();
        unidade.setId(1);
        unidade.setUnidade("kg");

        Insumo insumo = new Insumo();
        insumo.setId(id);
        insumo.setQtdAtual(qtdAtual);
        insumo.setEstoqueMinimo(20.0);
        insumo.setUnidadeMedida(unidade);
        return insumo;
    }

    private FogazzaInsumo registro(Insumo insumo, String quantidadeInsumo) {
        Fogazzas fogazza = new Fogazzas();
        fogazza.setId(1);

        FogazzaInsumo registro = new FogazzaInsumo();
        registro.setFogazza(fogazza);
        registro.setInsumo(insumo);
        registro.setQuantidadeInsumo(new BigDecimal(quantidadeInsumo));
        return registro;
    }

    private ItemVendido itemComReceita(String nome) {
        Fogazzas fogazza = new Fogazzas();
        fogazza.setId(1);
        fogazza.setNome(nome);

        Insumo insumo = insumo(10, 100.0);
        FogazzaInsumo registro = registro(insumo, "2");

        when(fogazzasRepository.findByNomeIgnoreCase(nome)).thenReturn(Optional.of(fogazza));
        when(fogazzaInsumoRepository.findByFogazzaId(1)).thenReturn(List.of(registro));
        when(insumoRepository.findById(10)).thenReturn(Optional.of(insumo));
        return new ItemVendido(nome, BigDecimal.ONE);
    }

    private MockMultipartFile arquivo(String caminho, String nomeArquivo) throws IOException {
        InputStream in = getClass().getResourceAsStream(caminho);
        Assertions.assertNotNull(in, "Recurso de teste não encontrado: " + caminho);
        byte[] bytes;
        try (in) {
            bytes = in.readAllBytes();
        }
        return new MockMultipartFile(
                "planilha",
                nomeArquivo,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bytes);
    }

    private MockMultipartFile arquivoPlanilha(String nomeArquivo, String[] cabecalho, Object[][] linhas)
            throws IOException {
        byte[] bytes;
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet aba = workbook.createSheet("Sheet");
            Row linhaCabecalho = aba.createRow(0);
            for (int coluna = 0; coluna < cabecalho.length; coluna++) {
                linhaCabecalho.createCell(coluna).setCellValue(cabecalho[coluna]);
            }
            for (int i = 0; i < linhas.length; i++) {
                Row linha = aba.createRow(i + 1);
                for (int coluna = 0; coluna < linhas[i].length; coluna++) {
                    Object valor = linhas[i][coluna];
                    if (valor instanceof String texto) {
                        linha.createCell(coluna).setCellValue(texto);
                    } else if (valor instanceof Number numero) {
                        linha.createCell(coluna).setCellValue(numero.doubleValue());
                    }
                }
            }

            workbook.write(out);
            bytes = out.toByteArray();
        }
        return new MockMultipartFile(
                "planilha",
                nomeArquivo,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bytes);
    }

    private MockMultipartFile planilhaComLinhasInvalidas() throws IOException {
        return arquivoPlanilha("artificial.xlsx",
                new String[]{"Data/Hora Item", "Qtd.", "Valor Un. Item", "Valor. Tot. Item",
                        "Tipo de Item", "Nome Prod"},
                new Object[][]{
                        {"01/07/2026 19:32", 2, 32.90, 65.80, "Comida", "Fogazza de Mussarela"},
                        {"01/07/2026 19:40", "abc", 32.90, 65.80, "Comida", "Fogazza de Portuguesa"},
                        {"01/07/2026 19:50", 5, 32.90, 65.80, "Comida", ""},
                        {null, null, 32.90, 65.80, "Comida", null},
                        {"01/07/2026 20:00", 0, 32.90, 65.80, "Comida", "Fogazza de Calabresa"},
                        {"01/07/2026 20:10", -1, 32.90, 65.80, "Comida", "Fogazza de Escarola"}
                });
    }

    private BigDecimal quantidadeDe(List<ItemVendido> itens, String nome) {
        return itens.stream()
                .filter(item -> item.getNomeItem().equals(nome))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Item não encontrado: " + nome))
                .getQuantidade();
    }
}
