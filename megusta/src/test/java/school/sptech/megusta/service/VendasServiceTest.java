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
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import school.sptech.megusta.dto.planilha_vendas.ItemVendido;
import school.sptech.megusta.exception.AcessoNegadoException;
import school.sptech.megusta.exception.EstoqueInsuficienteException;
import school.sptech.megusta.model.FogazzaInsumo;
import school.sptech.megusta.model.Fogazzas;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.Motivo;
import school.sptech.megusta.model.SaidaEstoque;
import school.sptech.megusta.model.TipoStatus;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.repository.FogazzaInsumoRepository;
import school.sptech.megusta.repository.FogazzasRepository;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.MotivoRepository;
import school.sptech.megusta.repository.SaidaEstoqueRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
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
    // 3.1 - Extração genérica da planilha (delega ao PlanilhaVendasExtractor)
    // ---------------------------------------------------------------

    // ---------------------------------------------------------------
    // 3.3 - Leitor do formato histórico de itens vendidos
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve extrair itens do formato histórico de itens vendidos")
    void deveExtrairItensDoFormatoHistorico() throws IOException {
        List<ItemVendido> itens = vendasService.lerPlanilha(
                arquivo("/historico_itens_vendidos.xlsx", "historico.xlsx"));

        Assertions.assertEquals(1, itens.size());
        Assertions.assertEquals("Fogazza Palmito com Catupiry", itens.get(0).getNomeItem());
        Assertions.assertEquals(BigDecimal.valueOf(1.0), itens.get(0).getQuantidade());
    }

    // ---------------------------------------------------------------
    // 3.4 - Leitor do formato de pedidos recentes
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve extrair itens do formato de pedidos recentes contando 1 unidade por ocorrência")
    void deveExtrairItensDoFormatoPedidosRecentes() throws IOException {
        List<ItemVendido> itens = vendasService.lerPlanilha(
                arquivo("/pedidos_recentes.xlsx", "pedidos.xlsx"));

        Assertions.assertEquals(2, itens.size());
        Assertions.assertTrue(itens.contains(new ItemVendido("Fogazza de Mussarela (Pizza)", BigDecimal.ONE)));
        Assertions.assertTrue(itens.contains(new ItemVendido("Fogazza de Portuguesa", BigDecimal.ONE)));
    }

    // ---------------------------------------------------------------
    // 3.5 - Leitor do formato de relatório de cardápio
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve extrair itens do relatório de cardápio somando as abas Itens e Complementos")
    void deveExtrairItensDoFormatoRelatorioCardapio() throws IOException {
        List<ItemVendido> itens = vendasService.lerPlanilha(
                arquivo("/relatorio_cardapio.xlsx", "relatorio.xlsx"));

        Assertions.assertEquals(42, itens.size());
        // soma das abas "Itens" (10) e "Complementos" (14)
        Assertions.assertEquals(BigDecimal.valueOf(24.0), quantidadeDe(itens, "Fogazza de Mussarela (Pizza)"));
        Assertions.assertEquals(BigDecimal.valueOf(14.0), quantidadeDe(itens, "Fogazza de Portuguesa"));
        Assertions.assertEquals(BigDecimal.valueOf(21.0), quantidadeDe(itens, "Não enviar Ketchup e mostarda"));
        Assertions.assertEquals(BigDecimal.valueOf(1.0), quantidadeDe(itens, "Coca-Cola Original 350ml"));
    }

    // ---------------------------------------------------------------
    // 3.6 - Linhas inválidas (quantidade não numérica, sem nome, em branco)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve ignorar linhas sem nome, com quantidade não numérica, zero ou em branco")
    void deveIgnorarLinhasInvalidas() throws IOException {
        List<ItemVendido> itens = vendasService.lerPlanilha(planilhaComLinhasInvalidas());

        Assertions.assertEquals(1, itens.size());
        Assertions.assertEquals("Fogazza de Mussarela", itens.get(0).getNomeItem());
        Assertions.assertEquals(BigDecimal.valueOf(2.0), itens.get(0).getQuantidade());
    }

    // ---------------------------------------------------------------
    // 4.1/4.4 - Orquestração: consultas, acumulação e persistência
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

        vendasService.baixarEstoque(List.of(item));

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
    }

    // ---------------------------------------------------------------
    // 4.2 - Resolução do usuário autenticado
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
    // 4.3 - Resolução do motivo "Venda"
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
    // 4.5 - Itens sem fogazza cadastrada e fogazza sem insumos
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

        Assertions.assertDoesNotThrow(() ->
                vendasService.baixarEstoque(List.of(encontrada, naoEncontrada)));

        verify(fogazzasRepository).findByNomeIgnoreCase("Fogazza de Mussarela");
        verify(fogazzasRepository).findByNomeIgnoreCase("Refrigerante Coca-Cola 350ml");
        verify(insumoRepository).save(insumo);
        Assertions.assertEquals(98.0, insumo.getQtdAtual());
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

        vendasService.baixarEstoque(List.of(new ItemVendido("Fogazza de Mussarela", BigDecimal.ONE)));

        verify(insumoRepository, never()).save(any(Insumo.class));
        verify(saidaEstoqueRepository, never()).save(any(SaidaEstoque.class));
    }

    // ---------------------------------------------------------------
    // 4.6 - Comportamento atômico (rollback)
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
    // 4.7 - Estoque insuficiente (qtdAtual < consumo da importação)
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

        Assertions.assertDoesNotThrow(() -> vendasService.baixarEstoque(List.of(item)));

        Assertions.assertEquals(0.0, insumo.getQtdAtual());
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
        Insumo insumo = new Insumo();
        insumo.setId(id);
        insumo.setQtdAtual(qtdAtual);
        insumo.setEstoqueMinimo(20.0);
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

    private InputStream getResource(String caminho) {
        InputStream in = getClass().getResourceAsStream(caminho);
        Assertions.assertNotNull(in, "Recurso de teste não encontrado: " + caminho);
        return in;
    }

    private MockMultipartFile arquivo(String caminho, String nomeArquivo) throws IOException {
        byte[] bytes;
        try (InputStream in = getResource(caminho)) {
            bytes = in.readAllBytes();
        }
        return new MockMultipartFile(
                "planilha",
                nomeArquivo,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bytes);
    }

    private MockMultipartFile planilhaComLinhasInvalidas() throws IOException {
        byte[] bytes;
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet aba = workbook.createSheet("Sheet");
            Row cabecalho = aba.createRow(0);
            cabecalho.createCell(0).setCellValue("Data/Hora Item");
            cabecalho.createCell(1).setCellValue("Qtd.");
            cabecalho.createCell(5).setCellValue("Nome Prod");

            Row valida = aba.createRow(1);
            valida.createCell(1).setCellValue(2);
            valida.createCell(5).setCellValue("Fogazza de Mussarela");

            Row naoNumerica = aba.createRow(2);
            naoNumerica.createCell(1).setCellValue("abc");
            naoNumerica.createCell(5).setCellValue("Fogazza de Portuguesa");

            Row semNome = aba.createRow(3);
            semNome.createCell(1).setCellValue(5);

            aba.createRow(4);

            Row quantidadeZero = aba.createRow(5);
            quantidadeZero.createCell(1).setCellValue(0);
            quantidadeZero.createCell(5).setCellValue("Fogazza de Calabresa");

            workbook.write(out);
            bytes = out.toByteArray();
        }
        return new MockMultipartFile(
                "planilha",
                "artificial.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bytes);
    }

    private BigDecimal quantidadeDe(List<ItemVendido> itens, String nome) {
        return itens.stream()
                .filter(item -> item.getNomeItem().equals(nome))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Item não encontrado: " + nome))
                .getQuantidade();
    }
}