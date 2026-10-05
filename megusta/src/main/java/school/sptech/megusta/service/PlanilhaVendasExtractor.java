package school.sptech.megusta.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import school.sptech.megusta.dto.planilha_vendas.ItemVendido;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Leitor do relatório de itens vendidos em planilhas {@code .xlsx}.
 *
 * <p>O formato reconhecido é estrito: o arquivo só é aceito quando alguma aba
 * tem, na linha de cabeçalho, a coluna de nome do produto ({@code Nome Prod}) e
 * a coluna de quantidade vendida ({@code Qtd.} ou {@code Qtd}). O casamento é
 * insensível a maiúsculas/minúsculas e ignora espaços nas extremidades, mas o
 * índice das colunas é indiferente — elas podem estar em qualquer posição e em
 * qualquer ordem relativa. Nenhum outro layout é aceito.
 *
 * <p>Regras de leitura:
 * <ul>
 *   <li>Cada aba que casar com a assinatura contribui com suas linhas; abas que
 *       não casarem são ignoradas. Se nenhuma aba casar, o arquivo é considerado
 *       não reconhecido (ver {@link #reconhecer(Workbook)}).</li>
 *   <li>Somente as duas colunas reconhecidas são lidas — todas as demais são
 *       ignoradas, independentemente de posição ou conteúdo.</li>
 *   <li>Linhas inválidas (nome em branco, quantidade não numérica, zero ou
 *       negativa) são ignoradas.</li>
 *   <li>O resultado é agregado por nome (soma das quantidades), mesmo para
 *       itens repetidos entre abas diferentes.</li>
 * </ul>
 */
@Component
public class PlanilhaVendasExtractor {

    /** Cabeçalho normalizado da coluna de nome do produto. */
    static final String CABECALHO_NOME_PROD = "nome prod";

    /** Cabeçalhos normalizados aceitos para a coluna de quantidade vendida. */
    static final List<String> CABECALHOS_QUANTIDADE = List.of("qtd.", "qtd");

    /** Índice de coluna inexistente — aba sem a assinatura do relatório. */
    static final int SEM_FORMATO_RECONHECIDO = -1;

    /**
     * Índices das colunas de nome e de quantidade reconhecidos em uma aba.
     *
     * @param aba              nome da aba que casou com a assinatura
     * @param colunaNome       índice da coluna de nome do produto
     * @param colunaQuantidade índice da coluna de quantidade vendida
     */
    record ColunasReconhecidas(String aba, int colunaNome, int colunaQuantidade) {}

    private final DataFormatter dataFormatter = new DataFormatter();

    /**
     * Reconhece o relatório de itens vendidos no workbook: para cada aba,
     * localiza pelo cabeçalho as colunas de nome do produto e de quantidade.
     * A lista devolvida guarda os índices reconhecidos para {@link
     * #extrair(Workbook, List)}.
     *
     * @param workbook planilha {@code .xlsx} aberta
     * @return uma entrada por aba que casou com a assinatura; lista vazia
     *         significa que o arquivo <b>não</b> é um relatório de itens vendidos
     */
    public List<ColunasReconhecidas> reconhecer(Workbook workbook) {
        List<ColunasReconhecidas> reconhecidas = new ArrayList<>();
        if (workbook == null) {
            return reconhecidas;
        }
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet aba = workbook.getSheetAt(i);
            int colunaNome = localizarColunaNome(aba);
            int colunaQuantidade = localizarColunaQuantidade(aba);
            if (colunaNome != SEM_FORMATO_RECONHECIDO && colunaQuantidade != SEM_FORMATO_RECONHECIDO) {
                reconhecidas.add(new ColunasReconhecidas(aba.getSheetName(), colunaNome, colunaQuantidade));
            }
        }
        return reconhecidas;
    }

    /**
     * Reconhece o formato e extrai os itens vendidos de todas as abas que
     * casaram com a assinatura, agregados por nome.
     *
     * @param workbook planilha {@code .xlsx} aberta
     * @return itens vendidos agregados por nome; lista vazia quando o arquivo
     *         não é reconhecido ou quando nenhuma linha é válida
     */
    public List<ItemVendido> extrair(Workbook workbook) {
        return extrair(workbook, reconhecer(workbook));
    }

    /**
     * Extrai os itens vendidos das abas já reconhecidas, agregados por nome.
     *
     * @param workbook      planilha {@code .xlsx} aberta
     * @param reconhecidas abas reconhecidas por {@link #reconhecer(Workbook)}
     * @return itens vendidos agregados por nome
     */
    public List<ItemVendido> extrair(Workbook workbook, List<ColunasReconhecidas> reconhecidas) {
        List<ItemVendido> itens = new ArrayList<>();
        if (workbook == null || reconhecidas == null || reconhecidas.isEmpty()) {
            return itens;
        }
        for (ColunasReconhecidas colunas : reconhecidas) {
            Sheet aba = workbook.getSheet(colunas.aba());
            if (aba != null) {
                itens.addAll(extrairAba(aba, colunas));
            }
        }
        return ItemVendido.agregarPorNome(itens);
    }

    /**
     * Localiza a coluna de nome do produto pelo cabeçalho normalizado
     * ({@code Nome Prod}). Retorna {@link #SEM_FORMATO_RECONHECIDO} quando a aba
     * não tem essa coluna.
     */
    int localizarColunaNome(Sheet aba) {
        return localizarColuna(aba, cabecalho -> CABECALHO_NOME_PROD.equals(cabecalho));
    }

    /**
     * Localiza a coluna de quantidade vendida pelo cabeçalho normalizado
     * ({@code Qtd.} ou {@code Qtd}). Retorna {@link #SEM_FORMATO_RECONHECIDO}
     * quando a aba não tem essa coluna.
     */
    int localizarColunaQuantidade(Sheet aba) {
        return localizarColuna(aba, CABECALHOS_QUANTIDADE::contains);
    }

    private int localizarColuna(Sheet aba, Predicate<String> casa) {
        Row cabecalho = aba.getRow(0);
        if (cabecalho == null) {
            return SEM_FORMATO_RECONHECIDO;
        }
        for (int coluna = 0; coluna < cabecalho.getLastCellNum(); coluna++) {
            if (casa.test(normalizar(valorTexto(cabecalho.getCell(coluna))))) {
                return coluna;
            }
        }
        return SEM_FORMATO_RECONHECIDO;
    }

    /**
     * Lê as linhas de uma aba a partir das duas colunas reconhecidas. Linhas com
     * nome em branco ou quantidade ausente, não numérica, zero ou negativa são
     * ignoradas.
     */
    private List<ItemVendido> extrairAba(Sheet aba, ColunasReconhecidas colunas) {
        List<ItemVendido> itens = new ArrayList<>();
        for (int i = 1; i <= aba.getLastRowNum(); i++) {
            Row linha = aba.getRow(i);
            if (linha == null) {
                continue;
            }
            String nome = valorTexto(linha.getCell(colunas.colunaNome()));
            if (nome == null || nome.isBlank()) {
                continue;
            }
            BigDecimal quantidade = valorQuantidade(linha.getCell(colunas.colunaQuantidade()));
            if (quantidade == null || quantidade.signum() <= 0) {
                continue;
            }
            itens.add(new ItemVendido(nome, quantidade));
        }
        return itens;
    }

    private String valorTexto(Cell celula) {
        if (celula == null) {
            return null;
        }
        return switch (celula.getCellType()) {
            case STRING -> celula.getStringCellValue().trim();
            case NUMERIC -> dataFormatter.formatCellValue(celula).trim();
            case BOOLEAN -> String.valueOf(celula.getBooleanCellValue());
            case FORMULA -> celula.getCellFormula();
            default -> null;
        };
    }

    /**
     * Interpreta o valor de uma célula de quantidade. Valores não numéricos (ou
     * ausentes) retornam {@code null} — a linha correspondente é ignorada.
     * Textos como {@code "1,5"} (vírgula decimal) são convertidos para
     * {@link BigDecimal}.
     */
    private BigDecimal valorQuantidade(Cell celula) {
        if (celula == null) {
            return null;
        }
        return switch (celula.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(celula.getNumericCellValue());
            case STRING -> {
                String texto = celula.getStringCellValue().trim();
                if (texto.isEmpty()) {
                    yield null;
                }
                try {
                    yield new BigDecimal(texto.replace(",", "."));
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    /**
     * Normaliza o cabeçalho para comparação: apenas espaços nas extremidades e
     * caixa. {@link Locale#ROOT} é usado para que o resultado não dependa do
     * locale do sistema (o servidor roda em {@code pt_BR}).
     */
    private String normalizar(String valor) {
        return valor == null ? null : valor.trim().toLowerCase(Locale.ROOT);
    }
}
