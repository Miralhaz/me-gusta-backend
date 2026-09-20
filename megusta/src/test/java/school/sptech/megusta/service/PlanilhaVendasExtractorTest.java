package school.sptech.megusta.service;

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
import java.util.List;

import static school.sptech.megusta.service.PlanilhaVendasExtractor.SEM_COLUNA;

/**
 * Testes do extrator genérico de planilhas de vendas: heurística de colunas,
 * extração por aba, modo {@code ;}-separado e as três fixtures de regressão
 * dos layouts anteriormente suportados.
 */
@DisplayName("Testes de PlanilhaVendasExtractor")
class PlanilhaVendasExtractorTest {

    private final PlanilhaVendasExtractor extractor = new PlanilhaVendasExtractor();

    // ---------------------------------------------------------------
    // Regressão: as três fixtures produzem os mesmos itens de antes
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Histórico de itens vendidos: 1 item com a quantidade da coluna Qtd.")
    void deveExtrairHistoricoItensVendidos() throws Exception {
        try (Workbook workbook = abrir("/historico_itens_vendidos.xlsx")) {
            List<ItemVendido> itens = extractor.extrair(workbook);

            Assertions.assertEquals(1, itens.size());
            Assertions.assertEquals("Fogazza Palmito com Catupiry", itens.get(0).getNomeItem());
            Assertions.assertEquals(BigDecimal.valueOf(1.0), itens.get(0).getQuantidade());
        }
    }

    @Test
    @DisplayName("Pedidos recentes: 1 unidade por ocorrência de cada nome na célula ;-separada")
    void deveExtrairPedidosRecentes() throws Exception {
        try (Workbook workbook = abrir("/pedidos_recentes.xlsx")) {
            List<ItemVendido> itens = extractor.extrair(workbook);

            Assertions.assertEquals(2, itens.size());
            Assertions.assertTrue(itens.contains(new ItemVendido("Fogazza de Mussarela (Pizza)", BigDecimal.ONE)));
            Assertions.assertTrue(itens.contains(new ItemVendido("Fogazza de Portuguesa", BigDecimal.ONE)));
        }
    }

    @Test
    @DisplayName("Relatório de cardápio: soma as abas Itens e Complementos e ignora Funil Loja")
    void deveExtrairRelatorioCardapio() throws Exception {
        try (Workbook workbook = abrir("/relatorio_cardapio.xlsx")) {
            List<ItemVendido> itens = extractor.extrair(workbook);

            Assertions.assertEquals(42, itens.size());
            Assertions.assertEquals(BigDecimal.valueOf(24.0), quantidadeDe(itens, "Fogazza de Mussarela (Pizza)"));
            Assertions.assertEquals(BigDecimal.valueOf(14.0), quantidadeDe(itens, "Fogazza de Portuguesa"));
            Assertions.assertEquals(BigDecimal.valueOf(21.0), quantidadeDe(itens, "Não enviar Ketchup e mostarda"));
            Assertions.assertEquals(BigDecimal.valueOf(1.0), quantidadeDe(itens, "Coca-Cola Original 350ml"));
        }
    }

    // ---------------------------------------------------------------
    // Heurística: as colunas corretas são escolhidas em cada aba das fixtures
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Histórico: coluna de nome (Nome Prod) e de quantidade (Qtd.) identificadas")
    void deveIdentificarColunasDoHistorico() throws Exception {
        try (Workbook workbook = abrir("/historico_itens_vendidos.xlsx")) {
            Sheet aba = workbook.getSheetAt(0);

            Assertions.assertEquals(5, extractor.identificarColunaNome(aba));
            Assertions.assertEquals(1, extractor.identificarColunaQuantidade(aba));
        }
    }

    @Test
    @DisplayName("Pedidos recentes: coluna de nome (Itens) identificada")
    void deveIdentificarColunaDePedidosRecentes() throws Exception {
        try (Workbook workbook = abrir("/pedidos_recentes.xlsx")) {
            Sheet aba = workbook.getSheetAt(0);

            Assertions.assertEquals(8, extractor.identificarColunaNome(aba));
        }
    }

    @Test
    @DisplayName("Relatório de cardápio: Itens (3/7), Complementos (3/6) e Funil Loja sem coluna de nome")
    void deveIdentificarColunasDoRelatorio() throws Exception {
        try (Workbook workbook = abrir("/relatorio_cardapio.xlsx")) {

            Sheet itens = workbook.getSheet("Itens");
            Assertions.assertEquals(3, extractor.identificarColunaNome(itens));
            Assertions.assertEquals(7, extractor.identificarColunaQuantidade(itens));

            Sheet complementos = workbook.getSheet("Complementos");
            Assertions.assertEquals(3, extractor.identificarColunaNome(complementos));
            Assertions.assertEquals(6, extractor.identificarColunaQuantidade(complementos));

            Sheet funilLoja = workbook.getSheet("Funil Loja");
            Assertions.assertEquals(SEM_COLUNA, extractor.identificarColunaNome(funilLoja));
        }
    }

    // ---------------------------------------------------------------
    // Layouts arbitrários / renomeados / reordenados
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Layout desconhecido com colunas identificáveis é extraído normalmente")
    void deveExtrairLayoutDesconhecidoComColunasIdentificaveis() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Vendas",
                    new String[]{"Nome do produto", "Vendidos"},
                    new Object[][]{
                            {"Fogazza de Frango", 2},
                            {"Fogazza de Carne", 3},
                            {"", 5},              // nome em branco → ignorado
                            {"Fogazza de Calabresa", "abc"}, // quantidade não numérica → ignorada
                            {"Fogazza de Escarola", 0},      // quantidade zero → ignorada
                            {"Fogazza de Doce de leite", -1} // quantidade negativa → ignorada
                    });

            Sheet aba = workbook.getSheetAt(0);
            Assertions.assertEquals(0, extractor.identificarColunaNome(aba));
            Assertions.assertEquals(1, extractor.identificarColunaQuantidade(aba));

            List<ItemVendido> itens = extractor.extrair(workbook);
            Assertions.assertEquals(2, itens.size());
            Assertions.assertEquals(BigDecimal.valueOf(2.0), quantidadeDe(itens, "Fogazza de Frango"));
            Assertions.assertEquals(BigDecimal.valueOf(3.0), quantidadeDe(itens, "Fogazza de Carne"));
        }
    }

    @Test
    @DisplayName("Colunas renomeadas e reordenadas continuam sendo identificadas")
    void deveExtrairComColunasRenomeadasEReordenadas() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Vendas",
                    new String[]{"Quantidade", "Descrição do item"},
                    new Object[][]{
                            {4, "Fogazza de Mussarela"},
                            {1.5, "Fogazza de Portuguesa"}
                    });

            Sheet aba = workbook.getSheetAt(0);
            Assertions.assertEquals(1, extractor.identificarColunaNome(aba));
            Assertions.assertEquals(0, extractor.identificarColunaQuantidade(aba));

            List<ItemVendido> itens = extractor.extrair(workbook);
            Assertions.assertEquals(2, itens.size());
            Assertions.assertEquals(BigDecimal.valueOf(4.0), quantidadeDe(itens, "Fogazza de Mussarela"));
            Assertions.assertEquals(new BigDecimal("1.5"), quantidadeDe(itens, "Fogazza de Portuguesa"));
        }
    }

    // ---------------------------------------------------------------
    // Workbook multi-abas com aba de dashboard e itens repetidos entre abas
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Workbook multi-abas ignora a aba de dashboard e soma itens repetidos entre abas")
    void deveIgnorarAbaDashboardESomarItensEntreAbas() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Funil Loja",
                    new String[]{"Período", "Nome da Loja", "Visitas", "Conversão"},
                    new Object[][]{
                            {"01/07 - 31/07", "Me Gusta Fogazzas Artesanais", "xxx", 1.434}
                    });
            adicionarAba(workbook, "Itens",
                    new String[]{"Período", "Nome do item", "Vendas total (quantidade)"},
                    new Object[][]{
                            {"01/07 - 31/07", "Fogazza de Mussarela", 2},
                            {"01/07 - 31/07", "Fogazza de Portuguesa", 3}
                    });
            adicionarAba(workbook, "Complementos",
                    new String[]{"Período", "Nome do complemento", "Vendas Total (Quantidade)"},
                    new Object[][]{
                            {"01/07 - 31/07", "Fogazza de Mussarela", 5}
                    });

            Sheet funil = workbook.getSheet("Funil Loja");
            Assertions.assertEquals(SEM_COLUNA, extractor.identificarColunaNome(funil));

            List<ItemVendido> itens = extractor.extrair(workbook);
            Assertions.assertEquals(2, itens.size());
            Assertions.assertEquals(BigDecimal.valueOf(7.0), quantidadeDe(itens, "Fogazza de Mussarela"));
            Assertions.assertEquals(BigDecimal.valueOf(3.0), quantidadeDe(itens, "Fogazza de Portuguesa"));
        }
    }

    // ---------------------------------------------------------------
    // Células ;-separadas (modo 1 unidade por ocorrência)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Célula ;-separada conta 1 unidade por ocorrência e ignora a coluna de quantidade")
    void deveContarUmaUnidadePorOcorrenciaNaCelulaPontoEVirgula() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Pedidos",
                    new String[]{"Número do pedido", "Itens", "Valor pago"},
                    new Object[][]{
                            {"6406", "Fogazza de Mussarela;Fogazza de Portuguesa;", "R$ 65,28"},
                            {"6407", "Fogazza de Mussarela", "R$ 32,90"},
                            {"6408", "", "R$ 10,00"}
                    });

            Sheet aba = workbook.getSheetAt(0);
            Assertions.assertEquals(1, extractor.identificarColunaNome(aba));

            List<ItemVendido> itens = extractor.extrair(workbook);
            Assertions.assertEquals(2, itens.size());
            Assertions.assertEquals(BigDecimal.valueOf(2), quantidadeDe(itens, "Fogazza de Mussarela"));
            Assertions.assertEquals(BigDecimal.valueOf(1), quantidadeDe(itens, "Fogazza de Portuguesa"));
        }
    }

    // ---------------------------------------------------------------
    // Planilhas sem colunas de item/quantidade → lista vazia
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Planilha sem colunas identificáveis de item/quantidade retorna lista vazia")
    void deveRetornarListaVaziaQuandoNaoHaColunasIdentificaveis() throws Exception {
        try (Workbook workbook = novoWorkbook()) {
            adicionarAba(workbook, "Funil Loja",
                    new String[]{"Período", "Nome da Loja", "Visitas", "Conversão"},
                    new Object[][]{
                            {"01/07 - 31/07", "Me Gusta Fogazzas Artesanais", "xxx", 1.434}
                    });
            adicionarAba(workbook, "Outra",
                    new String[]{"Cidade", "Estado"},
                    new Object[][]{
                            {"SAO PAULO", "SP"}
                    });

            List<ItemVendido> itens = extractor.extrair(workbook);
            Assertions.assertTrue(itens.isEmpty());
        }
    }

    @Test
    @DisplayName("Workbook somente com cabeçalho ou sem abas retorna lista vazia")
    void deveRetornarListaVaziaParaWorkbookVazio() throws Exception {
        try (Workbook somenteCabecalho = novoWorkbook()) {
            adicionarAba(somenteCabecalho, "Vendas",
                    new String[]{"Período", "Nome do item", "Qtd."},
                    new Object[][]{});
            Assertions.assertTrue(extractor.extrair(somenteCabecalho).isEmpty());
        }

        try (Workbook semAbas = novoWorkbook()) {
            Assertions.assertTrue(extractor.extrair(semAbas).isEmpty());
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Workbook abrir(String caminho) throws Exception {
        InputStream in = getClass().getResourceAsStream(caminho);
        Assertions.assertNotNull(in, "Recurso de teste não encontrado: " + caminho);
        return WorkbookFactory.create(in);
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