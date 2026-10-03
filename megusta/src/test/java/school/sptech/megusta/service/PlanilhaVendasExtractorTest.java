package school.sptech.megusta.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.sptech.megusta.dto.planilha_vendas.ItemVendido;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static school.sptech.megusta.service.PlanilhaVendasExtractor.SEM_FORMATO_RECONHECIDO;

/**
 * Testes do leitor do relatório de itens vendidos: o formato único reconhecido
 * pela assinatura {@code Nome Prod} + {@code Qtd.}, a varredura multi-aba, a
 * leitura exclusiva das duas colunas reconhecidas e a rejeição de qualquer
 * outro layout.
 */
@DisplayName("Testes de PlanilhaVendasExtractor")
class PlanilhaVendasExtractorTest {

    private static final Path RAIZ_DO_REPOSITORIO = Path.of("..").toAbsolutePath().normalize();

    private final PlanilhaVendasExtractor extractor = new PlanilhaVendasExtractor();

    // ---------------------------------------------------------------
    // Formato reconhecido: colunas em qualquer posição e caixa
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Reconhece as colunas na posição original do relatório (Nome Prod = 5, Qtd. = 1)")
    void deveReconhecerColunasNaPosicaoOriginal() throws Exception {
        try (Workbook workbook = planilhaRealDoRepositorio()) {
            Sheet aba = workbook.getSheetAt(0);

            Assertions.assertEquals(5, extractor.localizarColunaNome(aba));
            Assertions.assertEquals(1, extractor.localizarColunaQuantidade(aba));
            Assertions.assertEquals(1, extractor.reconhecer(workbook).size());
        }
    }

    @Test
    @DisplayName("Reconhece cabeçalhos em outra caixa com as colunas reordenadas")
    void deveReconhecerCabecalhosEmOutraCaixaEColunasReordenadas() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Vendas",
                    new String[]{"Data/Hora Item", "NOME PROD", "Valor Un. Item", "qtd.", "Stat. Ped."},
                    new Object[][]{
                            {"01/07/2026 19:32", "Fogazza de Mussarela", "32,90", 2, "Concluído"},
                            {"01/07/2026 20:10", "Fogazza de Portuguesa", "34,90", 3, "Concluído"}
                    });

            Sheet aba = workbook.getSheetAt(0);
            Assertions.assertEquals(1, extractor.localizarColunaNome(aba));
            Assertions.assertEquals(3, extractor.localizarColunaQuantidade(aba));

            List<ItemVendido> itens = extractor.extrair(workbook);
            Assertions.assertEquals(2, itens.size());
            Assertions.assertEquals(BigDecimal.valueOf(2.0), quantidadeDe(itens, "Fogazza de Mussarela"));
            Assertions.assertEquals(BigDecimal.valueOf(3.0), quantidadeDe(itens, "Fogazza de Portuguesa"));
        }
    }

    @Test
    @DisplayName("Aceita o cabeçalho de quantidade sem o ponto final")
    void deveAceitarCabecalhoQuantidadeSemPonto() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Vendas",
                    new String[]{"Qtd", "Nome Prod"},
                    new Object[][]{{4, "Fogazza de Mussarela"}});

            Assertions.assertEquals(0, extractor.localizarColunaQuantidade(workbook.getSheetAt(0)));
            Assertions.assertEquals(1, extractor.localizarColunaNome(workbook.getSheetAt(0)));
            Assertions.assertEquals(1, extractor.extrair(workbook).size());
        }
    }

    @Test
    @DisplayName("Ignora espaços nas extremidades do cabeçalho")
    void deveIgnorarEspacosNasExtremidadesDoCabecalho() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Vendas",
                    new String[]{"  Nome Prod  ", " Qtd. "},
                    new Object[][]{{"Fogazza de Mussarela", 2}});

            Sheet aba = workbook.getSheetAt(0);
            Assertions.assertEquals(0, extractor.localizarColunaNome(aba));
            Assertions.assertEquals(1, extractor.localizarColunaQuantidade(aba));
            Assertions.assertEquals(1, extractor.extrair(workbook).size());
        }
    }

    // ---------------------------------------------------------------
    // Leitura exclusiva das duas colunas reconhecidas
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Lê apenas as duas colunas reconhecidas e ignora as demais")
    void deveLerSomenteAsDuasColunasReconhecidas() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Vendas",
                    new String[]{"Data/Hora Item", "Qtd.", "Valor Un. Item", "Valor. Tot. Item",
                            "Tipo de Item", "Nome Prod", "Tipo Prod", "Cat. Prod.", "Valor Prod",
                            "Cod. Ped.", "Núm. Mesa/Com.", "Data Ab. Ped.", "Data Fec. Ped.",
                            "Tipo Ped.", "Stat. Ped."},
                    new Object[][]{
                            {"01/07/2026 19:32", 2, 32.90, 65.80, "Comida", "Fogazza de Mussarela", "Fogazza", "Principal", 45.0, "6406", 3, "01/07/2026 19:32", "01/07/2026 20:10", " delivery ", "Concluído"},
                            {"01/07/2026 21:00", 1, 34.90, 34.90, "Comida", "Fogazza de Portuguesa", "Fogazza", "Principal", 45.0, "6407", 7, "01/07/2026 21:00", "01/07/2026 21:30", " delivery ", "Concluído"}
                    });

            List<ItemVendido> itens = extractor.extrair(workbook);

            Assertions.assertEquals(2, itens.size());
            Assertions.assertEquals(BigDecimal.valueOf(2.0), quantidadeDe(itens, "Fogazza de Mussarela"));
            Assertions.assertEquals(BigDecimal.valueOf(1.0), quantidadeDe(itens, "Fogazza de Portuguesa"));
        }
    }

    @Test
    @DisplayName("Ignora linhas com nome em branco, quantidade não numérica, zero ou negativa")
    void deveIgnorarLinhasInvalidas() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Sheet",
                    new String[]{"Data/Hora Item", "Qtd.", "Valor Un. Item", "Valor. Tot. Item",
                            "Tipo de Item", "Nome Prod"},
                    new Object[][]{
                            {"01/07/2026 19:32", 2, 32.90, 65.80, "Comida", "Fogazza de Mussarela"},
                            {"01/07/2026 19:40", "abc", 32.90, 65.80, "Comida", "Fogazza de Portuguesa"},
                            {"01/07/2026 19:50", 5, 32.90, 65.80, "Comida", ""},
                            {"01/07/2026 20:00", 0, 32.90, 65.80, "Comida", "Fogazza de Calabresa"},
                            {"01/07/2026 20:10", -1, 32.90, 65.80, "Comida", "Fogazza de Escarola"}
                    });

            List<ItemVendido> itens = extractor.extrair(workbook);

            Assertions.assertEquals(1, itens.size());
            Assertions.assertEquals("Fogazza de Mussarela", itens.get(0).getNomeItem());
            Assertions.assertEquals(BigDecimal.valueOf(2.0), itens.get(0).getQuantidade());
        }
    }

    @Test
    @DisplayName("Agrega o nome repetido somando as quantidades (1 + 2 + 3 = 6)")
    void deveAgregarNomeRepetidoSomandoQuantidades() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Sheet",
                    new String[]{"Qtd.", "Nome Prod"},
                    new Object[][]{
                            {1, "Fogazza de Mussarela"},
                            {2, "Fogazza de Mussarela"},
                            {3, "Fogazza de Mussarela"},
                            {5, "Fogazza de Portuguesa"}
                    });

            List<ItemVendido> itens = extractor.extrair(workbook);

            Assertions.assertEquals(2, itens.size());
            Assertions.assertEquals(new BigDecimal("6.0"), quantidadeDe(itens, "Fogazza de Mussarela"));
            Assertions.assertEquals(new BigDecimal("5.0"), quantidadeDe(itens, "Fogazza de Portuguesa"));
        }
    }

    @Test
    @DisplayName("Planilha reconhecida apenas com a linha de cabeçalho retorna lista vazia")
    void deveRetornarListaVaziaParaPlanilhaSomenteComCabecalho() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Sheet",
                    new String[]{"Data/Hora Item", "Qtd.", "Nome Prod"},
                    new Object[][]{});

            Assertions.assertEquals(1, extractor.reconhecer(workbook).size());
            Assertions.assertTrue(extractor.extrair(workbook).isEmpty());
        }
    }

    // ---------------------------------------------------------------
    // Varredura multi-aba (D8)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Reconhece workbook de uma aba que casa com a assinatura")
    void deveReconhecerWorkbookDeAbaUnicaQueCasa() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Sheet",
                    new String[]{"Qtd.", "Nome Prod"},
                    new Object[][]{{2, "Fogazza de Mussarela"}});

            Assertions.assertEquals(1, extractor.reconhecer(workbook).size());
            Assertions.assertEquals(1, extractor.extrair(workbook).size());
        }
    }

    @Test
    @DisplayName("Em workbook multi-aba, só as abas que casam com a assinatura contribuem")
    void deveConsiderarSomenteAbasQueCasamComAAassinatura() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Funil Loja",
                    new String[]{"Período", "Nome da Loja", "Visitas", "Conversão"},
                    new Object[][]{{"01/07 - 31/07", "Me Gusta Fogazzas Artesanais", "xxx", 1.434}});
            adicionarAba(workbook, "Itens",
                    new String[]{"Nome do item", "Vendas total (quantidade)"},
                    new Object[][]{{"Fogazza de Frango", 99}});
            adicionarAba(workbook, "Julho",
                    new String[]{"Data/Hora Item", "Qtd.", "Nome Prod"},
                    new Object[][]{
                            {"01/07/2026 19:32", 2, "Fogazza de Mussarela"},
                            {"01/07/2026 19:40", 3, "Fogazza de Portuguesa"}
                    });
            adicionarAba(workbook, "Agosto",
                    new String[]{"Data/Hora Item", "Qtd.", "Nome Prod"},
                    new Object[][]{{"02/08/2026 19:32", 5, "Fogazza de Mussarela"}});

            List<PlanilhaVendasExtractor.ColunasReconhecidas> reconhecidas = extractor.reconhecer(workbook);

            Assertions.assertEquals(2, reconhecidas.size());
            Assertions.assertEquals(List.of("Julho", "Agosto"),
                    reconhecidas.stream().map(PlanilhaVendasExtractor.ColunasReconhecidas::aba).toList());

            List<ItemVendido> itens = extractor.extrair(workbook, reconhecidas);
            Assertions.assertEquals(2, itens.size());
            Assertions.assertEquals(new BigDecimal("7.0"), quantidadeDe(itens, "Fogazza de Mussarela"));
            Assertions.assertEquals(new BigDecimal("3.0"), quantidadeDe(itens, "Fogazza de Portuguesa"));
        }
    }

    @Test
    @DisplayName("Workbook em que nenhuma aba casa com a assinatura é não reconhecido")
    void deveMarcarArquivoComoNaoReconhecidoQuandoNenhumaAbaCasa() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Funil Loja",
                    new String[]{"Período", "Nome da Loja", "Visitas", "Conversão"},
                    new Object[][]{{"01/07 - 31/07", "Me Gusta Fogazzas Artesanais", "xxx", 1.434}});
            adicionarAba(workbook, "Itens",
                    new String[]{"Nome do item", "Vendas total (quantidade)"},
                    new Object[][]{{"Fogazza de Mussarela", 24}});

            Assertions.assertTrue(extractor.reconhecer(workbook).isEmpty());
            Assertions.assertTrue(extractor.extrair(workbook).isEmpty());
        }
    }

    // ---------------------------------------------------------------
    // Rejeição de qualquer outro layout
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Planilha com Nome Prod mas sem Qtd. é rejeitada")
    void deveRejeitarPlanilhaComNomeProdMasSemQtd() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Sheet",
                    new String[]{"Itens", "Ganhos", "Nome Prod"},
                    new Object[][]{{"Fogazza de Mussarela;Fogazza de Portuguesa;", "65,28", "Pedido 6406"}});

            Sheet aba = workbook.getSheetAt(0);
            Assertions.assertEquals(2, extractor.localizarColunaNome(aba));
            Assertions.assertEquals(SEM_FORMATO_RECONHECIDO, extractor.localizarColunaQuantidade(aba));

            Assertions.assertTrue(extractor.reconhecer(workbook).isEmpty());
            Assertions.assertTrue(extractor.extrair(workbook).isEmpty());
        }
    }

    @Test
    @DisplayName("Planilha com cabeçalhos Produto e Valor Total é rejeitada")
    void deveRejeitarPlanilhaComCabecalhosProdutoEValorTotal() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Sheet",
                    new String[]{"Produto", "Valor Total"},
                    new Object[][]{
                            {"Fogazza de Mussarela", 65.80},
                            {"Fogazza de Portuguesa", 34.90}
                    });

            Assertions.assertTrue(extractor.reconhecer(workbook).isEmpty());
            Assertions.assertTrue(extractor.extrair(workbook).isEmpty());
        }
    }

    @Test
    @DisplayName("Planilha de pedidos recentes (coluna Itens ;-separada) é rejeitada")
    void deveRejeitarPlanilhaDePedidosRecentes() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Me Gusta Fogazzas Artesanais 15",
                    new String[]{"Número do pedido", "Nome da loja", "Status do pedido",
                            "Horário do pedido", "Itens", "Ganhos", "Valor pago pelo cliente"},
                    new Object[][]{
                            {"6406", "Me Gusta Fogazzas Artesanais", "Concluído", "19:32",
                                    "Fogazza de Mussarela;Fogazza de Portuguesa;", 65.28, 70.0},
                            {"6407", "Me Gusta Fogazzas Artesanais", "Concluído", "20:10",
                                    "Fogazza de Mussarela", 32.90, 35.0}
                    });

            Assertions.assertTrue(extractor.reconhecer(workbook).isEmpty());
            Assertions.assertTrue(extractor.extrair(workbook).isEmpty());
        }
    }

    @Test
    @DisplayName("Planilha real de Pedidos Recentes da raiz do repositório é rejeitada")
    void deveRejeitarPlanilhaRealDePedidosRecentes() throws Exception {
        Path arquivo = arquivoDoRepositorio("Me Gusta Fogazzas Artesanais", "Pedidos recentes");
        assumeTrue(arquivo != null,
                "Planilha real de Pedidos Recentes não encontrada na raiz do repositório");

        try (Workbook workbook = abrir(arquivo)) {
            Assertions.assertTrue(extractor.reconhecer(workbook).isEmpty());
            Assertions.assertTrue(extractor.extrair(workbook).isEmpty());
        }
    }

    // ---------------------------------------------------------------
    // Regressão com a planilha real do repositório
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Planilha real: extrai os 56 nomes distintos com a quantidade da coluna Qtd.")
    void deveExtrairOs56NomesDistintosDaPlanilhaReal() throws Exception {
        try (Workbook workbook = planilhaRealDoRepositorio()) {
            List<ItemVendido> itens = extractor.extrair(workbook);

            Assertions.assertEquals(56, itens.size());
            Assertions.assertEquals(56, itens.stream().map(ItemVendido::getNomeItem).distinct().count());
            Assertions.assertEquals(new BigDecimal("59.0"), quantidadeDe(itens, "Fogazza Mussarela (Pizza)"));
            Assertions.assertEquals(new BigDecimal("5.0"), quantidadeDe(itens, "Fogazza Palmito com Catupiry"));
        }
    }

    @Test
    @DisplayName("Planilha real: as 13 colunas não relacionadas não contribuem para a extração")
    void deveIgnorarAsColunasNaoRelacionadasDaPlanilhaReal() throws Exception {
        try (Workbook workbook = planilhaRealDoRepositorio()) {
            Sheet aba = workbook.getSheetAt(0);
            List<ItemVendido> itens = extractor.extrair(workbook);

            // 15 colunas no arquivo real; só "Qtd." (1) e "Nome Prod" (5) são lidas
            Assertions.assertEquals(15, aba.getRow(0).getLastCellNum());
            Assertions.assertEquals(1, extractor.localizarColunaQuantidade(aba));
            Assertions.assertEquals(5, extractor.localizarColunaNome(aba));

            // A soma extraída (769×1 + 65×2 + 7×3 = 920) só bate se a quantidade
            // vier exclusivamente da coluna "Qtd."; qualquer outra coluna numérica
            // do arquivo (valores, códigos, taxas) tornaria a soma incompatível.
            BigDecimal totalExtraido = itens.stream()
                    .map(ItemVendido::getQuantidade)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Assertions.assertEquals(new BigDecimal("920.0"), totalExtraido);

            // Nenhum nome extraído vem de uma das 13 colunas ignoradas
            Set<String> valoresDasColunasIgnoradas =
                    valoresDasColunasIgnoradas(aba, extractor.localizarColunaQuantidade(aba),
                            extractor.localizarColunaNome(aba));
            Assertions.assertFalse(valoresDasColunasIgnoradas.isEmpty());
            Assertions.assertTrue(itens.stream().map(ItemVendido::getNomeItem)
                    .noneMatch(valoresDasColunasIgnoradas::contains),
                    "Nenhum nome extraído pode vir de uma coluna que não é 'Nome Prod'");
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    /**
     * Abre a planilha real do relatório de itens vendidos da raiz do repositório,
     * localizada por glob — o nome literal carrega acento decomposto e muda a
     * cada período exportado.
     */
    private Workbook planilhaRealDoRepositorio() throws Exception {
        Path arquivo = arquivoDoRepositorio("Historico_Itens_Vendidos");
        assumeTrue(arquivo != null,
                "Planilha real de itens vendidos não encontrada na raiz do repositório");
        return abrir(arquivo);
    }

    private Path arquivoDoRepositorio(String prefixo, String... tambemContem) {
        try (Stream<Path> arquivos = Files.list(RAIZ_DO_REPOSITORIO)) {
            return arquivos
                    .filter(p -> p.getFileName().toString().endsWith(".xlsx"))
                    .filter(p -> {
                        String nome = p.getFileName().toString();
                        if (!nome.startsWith(prefixo)) {
                            return false;
                        }
                        for (String trecho : tambemContem) {
                            if (!nome.contains(trecho)) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private Workbook abrir(Path arquivo) throws Exception {
        try (InputStream in = Files.newInputStream(arquivo)) {
            return WorkbookFactory.create(in);
        }
    }

    /**
     * Coleta os valores textuais de todas as colunas da aba <b>exceto</b> as duas
     * reconhecidas — no arquivo real, as 13 colunas não relacionadas ao relatório
     * de itens vendidos.
     */
    private Set<String> valoresDasColunasIgnoradas(Sheet aba, int... colunasReconhecidas) {
        Set<String> valores = new HashSet<>();
        for (int i = 1; i <= aba.getLastRowNum(); i++) {
            Row linha = aba.getRow(i);
            if (linha == null) {
                continue;
            }
            for (int coluna = 0; coluna < aba.getRow(0).getLastCellNum(); coluna++) {
                boolean reconhecida = false;
                for (int colunaReconhecida : colunasReconhecidas) {
                    reconhecida |= coluna == colunaReconhecida;
                }
                Cell celula = linha.getCell(coluna);
                if (!reconhecida && celula != null && celula.getCellType() == CellType.STRING) {
                    String valor = celula.getStringCellValue().trim();
                    if (!valor.isEmpty()) {
                        valores.add(valor);
                    }
                }
            }
        }
        return valores;
    }

    private Workbook novoWorkbook() {
        return new XSSFWorkbook();
    }

    private void adicionarAba(Workbook workbook, String nome, String[] cabecalho, Object[][] linhas) {
        Sheet aba = workbook.createSheet(nome);
        Row linhaCabecalho = aba.createRow(0);
        for (int coluna = 0; coluna < cabecalho.length; coluna++) {
            linhaCabecalho.createCell(coluna).setCellValue(cabecalho[coluna]);
        }
        for (int i = 0; i < linhas.length; i++) {
            Row linha = aba.createRow(i + 1);
            Object[] valores = linhas[i];
            for (int coluna = 0; coluna < valores.length; coluna++) {
                Object valor = valores[coluna];
                if (valor instanceof String texto) {
                    linha.createCell(coluna).setCellValue(texto);
                } else if (valor instanceof Number numero) {
                    linha.createCell(coluna).setCellValue(numero.doubleValue());
                }
            }
        }
    }

    private BigDecimal quantidadeDe(List<ItemVendido> itens, String nome) {
        return itens.stream()
                .filter(item -> item.getNomeItem().equals(nome))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Item não encontrado: " + nome))
                .getQuantidade();
    }
}
