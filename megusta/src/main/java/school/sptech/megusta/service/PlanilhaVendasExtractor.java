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
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Extrator genérico e auto-adaptativo de itens vendidos a partir de planilhas
 * {@code .xlsx}. Substitui o reconhecimento de três formatos fixos: para cada
 * aba do workbook, identifica por heurística (cabeçalho na linha 0 + conteúdo
 * das células) a coluna de nome do item e a coluna de quantidade, sem depender
 * de layouts conhecidos.
 *
 * <p>Regras principais:
 * <ul>
 *   <li>Abas sem coluna de nome com corpo textual (ex.: dashboards como
 *       "Funil Loja") são ignoradas, em vez de falhar a importação.</li>
 *   <li>Células de nome com vários itens separados por {@code ;} contam
 *       1 unidade por ocorrência (a coluna de quantidade é ignorada nesse
 *       modo).</li>
 *   <li>Linhas inválidas (nome em branco, quantidade não numérica, zero ou
 *       negativa) são ignoradas.</li>
 *   <li>O resultado final é agregado por nome (soma das quantidades), mesmo
 *       para itens repetidos entre abas diferentes.</li>
 * </ul>
 */
@Component
public class PlanilhaVendasExtractor {

    /** Termos de cabeçalho que sugerem coluna de nome de item/produto. */
    private static final List<String> ALIASES_COLUNA_NOME = List.of(
            "item", "itens", "nome", "prod", "produto");

    /** Termos de cabeçalho de colunas que NÃO são de nome de item (metadados
     * de loja, funis, períodos etc.), fortemente penalizados na escolha. */
    private static final List<String> TERMOS_NAO_NOME = List.of(
            "loja", "period", "cidade", "estado", "marca", "funil",
            "visita", "visualiza", "sacola", "revis", "conclu", "convers",
            "anterior", "data", "horario", "status", "numero");

    /** Termos de cabeçalho que sugerem coluna de quantidade vendida. */
    private static final List<String> ALIASES_COLUNA_QUANTIDADE = List.of(
            "qtd", "quant", "vendas", "quantidade");

    /** Termos de cabeçalho de colunas de valor/preço, penalizados na escolha
     * da coluna de quantidade (para não confundir preço com quantidade). */
    private static final List<String> TERMOS_VALOR_PRECO = List.of(
            "valor", "preco", "total", "ganho", "r$");

    private static final int BONUS_ALIAS_NOME = 2;
    private static final int PENALIDADE_NAO_NOME = 3;
    private static final int BONUS_ALIAS_QUANTIDADE = 3;
    private static final int PENALIDADE_VALOR_PRECO = 2;

    /** Índice de coluna inexistente ({@code -1}). */
    static final int SEM_COLUNA = -1;

    private final DataFormatter dataFormatter = new DataFormatter();

    /**
     * Extrai os itens vendidos de todas as abas do workbook, agregados por
     * nome. Abas sem colunas identificáveis de item/quantidade são ignoradas.
     * Quando nenhuma aba produz pares válidos, retorna lista vazia.
     *
     * @param workbook planilha {@code .xlsx} aberta
     * @return itens vendidos agregados por nome
     */
    public List<ItemVendido> extrair(Workbook workbook) {
        List<ItemVendido> itens = new ArrayList<>();
        if (workbook == null) {
            return itens;
        }
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            itens.addAll(extrairAba(workbook.getSheetAt(i)));
        }
        return ItemVendido.agregarPorNome(itens);
    }

    /**
     * Extrai os pares (nome, quantidade) de uma única aba. Aba sem coluna de
     * nome com corpo textual (ou sem coluna de quantidade) é ignorada;
     * colunas de nome com células {@code ;}-separadas entram no modo de
     * contagem de 1 unidade por ocorrência.
     */
    List<ItemVendido> extrairAba(Sheet aba) {
        Row cabecalho = aba.getRow(0);
        if (cabecalho == null) {
            return List.of();
        }

        int colunaNome = identificarColunaNome(aba);
        if (colunaNome == SEM_COLUNA) {
            return List.of();
        }

        if (temCelulasComPontoEVirgula(aba, colunaNome)) {
            return extrairModoPontoEVirgula(aba, colunaNome);
        }

        int colunaQuantidade = identificarColunaQuantidade(aba);
        if (colunaQuantidade == SEM_COLUNA) {
            return List.of();
        }

        return extrairParesNomeQuantidade(aba, colunaNome, colunaQuantidade);
    }

    /**
     * Identifica por heurística a coluna de nome do item: colunas sem células
     * textuais no corpo não concorrem; a pontuação é a quantidade de células
     * textuais somada a um bônus por termo de cabeçalho de item/produto e
     * descontada por termos de metadados (loja, funil, períodos etc.).
     * Retorna {@link #SEM_COLUNA} quando nenhuma coluna pontua acima de zero.
     */
    int identificarColunaNome(Sheet aba) {
        Row cabecalho = aba.getRow(0);
        int melhorColuna = SEM_COLUNA;
        int melhorPontuacao = 0;

        for (int coluna = 0; cabecalho != null && coluna <= cabecalho.getLastCellNum(); coluna++) {
            int pontuacao = pontuarColunaNome(aba, cabecalho, coluna);
            if (pontuacao > melhorPontuacao) {
                melhorPontuacao = pontuacao;
                melhorColuna = coluna;
            }
        }
        return melhorColuna;
    }

    /**
     * Identifica por heurística a coluna de quantidade: colunas sem células
     * numéricas no corpo não concorrem; a pontuação é a quantidade de células
     * numéricas somada a um bônus por termo de cabeçalho de quantidade e
     * descontada por termos de valor/preço. Retorna {@link #SEM_COLUNA}
     * quando nenhuma coluna pontua acima de zero.
     */
    int identificarColunaQuantidade(Sheet aba) {
        Row cabecalho = aba.getRow(0);
        int melhorColuna = SEM_COLUNA;
        int melhorPontuacao = 0;

        for (int coluna = 0; cabecalho != null && coluna <= cabecalho.getLastCellNum(); coluna++) {
            int pontuacao = pontuarColunaQuantidade(aba, cabecalho, coluna);
            if (pontuacao > melhorPontuacao) {
                melhorPontuacao = pontuacao;
                melhorColuna = coluna;
            }
        }
        return melhorColuna;
    }

    private int pontuarColunaNome(Sheet aba, Row cabecalho, int coluna) {
        int celulasTextuais = contarCelulasTextuais(aba, coluna);
        if (celulasTextuais == 0) {
            return 0;
        }

        String titulo = normalizar(valorTexto(cabecalho.getCell(coluna)));
        if (titulo == null) {
            return 0;
        }

        int pontuacao = celulasTextuais;
        for (String alias : ALIASES_COLUNA_NOME) {
            if (titulo.contains(alias)) {
                pontuacao += BONUS_ALIAS_NOME;
            }
        }
        for (String termo : TERMOS_NAO_NOME) {
            if (titulo.contains(termo)) {
                pontuacao -= PENALIDADE_NAO_NOME;
            }
        }
        return pontuacao;
    }

    private int pontuarColunaQuantidade(Sheet aba, Row cabecalho, int coluna) {
        int celulasNumericas = contarCelulasNumericas(aba, coluna);
        if (celulasNumericas == 0) {
            return 0;
        }

        String titulo = normalizar(valorTexto(cabecalho.getCell(coluna)));
        if (titulo == null) {
            return 0;
        }

        int pontuacao = celulasNumericas;
        for (String alias : ALIASES_COLUNA_QUANTIDADE) {
            if (titulo.contains(alias)) {
                pontuacao += BONUS_ALIAS_QUANTIDADE;
            }
        }
        for (String termo : TERMOS_VALOR_PRECO) {
            if (titulo.contains(termo)) {
                pontuacao -= PENALIDADE_VALOR_PRECO;
            }
        }
        return pontuacao;
    }

    private List<ItemVendido> extrairParesNomeQuantidade(Sheet aba, int colunaNome, int colunaQuantidade) {
        List<ItemVendido> itens = new ArrayList<>();
        for (int i = 1; i <= aba.getLastRowNum(); i++) {
            Row linha = aba.getRow(i);
            if (linha == null) {
                continue;
            }
            String nome = valorTexto(linha.getCell(colunaNome));
            if (nome == null || nome.isBlank()) {
                continue;
            }
            BigDecimal quantidade = valorQuantidade(linha.getCell(colunaQuantidade));
            if (quantidade == null || quantidade.signum() <= 0) {
                continue;
            }
            itens.add(new ItemVendido(nome, quantidade));
        }
        return itens;
    }

    private List<ItemVendido> extrairModoPontoEVirgula(Sheet aba, int colunaNome) {
        List<ItemVendido> itens = new ArrayList<>();
        for (int i = 1; i <= aba.getLastRowNum(); i++) {
            Row linha = aba.getRow(i);
            if (linha == null) {
                continue;
            }
            String celula = valorTexto(linha.getCell(colunaNome));
            if (celula == null || celula.isBlank()) {
                continue;
            }
            for (String nome : celula.split(";")) {
                String nomeLimpo = nome.trim();
                if (!nomeLimpo.isEmpty()) {
                    itens.add(new ItemVendido(nomeLimpo, BigDecimal.ONE));
                }
            }
        }
        return itens;
    }

    private boolean temCelulasComPontoEVirgula(Sheet aba, int colunaNome) {
        for (int i = 1; i <= aba.getLastRowNum(); i++) {
            Row linha = aba.getRow(i);
            if (linha == null) {
                continue;
            }
            String celula = valorTexto(linha.getCell(colunaNome));
            if (celula != null && celula.contains(";")) {
                return true;
            }
        }
        return false;
    }

    private int contarCelulasTextuais(Sheet aba, int coluna) {
        int total = 0;
        for (int i = 1; i <= aba.getLastRowNum(); i++) {
            Row linha = aba.getRow(i);
            if (linha == null) {
                continue;
            }
            Cell celula = linha.getCell(coluna);
            if (celula != null && celula.getCellType() == CellType.STRING
                    && !celula.getStringCellValue().isBlank()) {
                total++;
            }
        }
        return total;
    }

    private int contarCelulasNumericas(Sheet aba, int coluna) {
        int total = 0;
        for (int i = 1; i <= aba.getLastRowNum(); i++) {
            Row linha = aba.getRow(i);
            if (linha == null) {
                continue;
            }
            Cell celula = linha.getCell(coluna);
            if (celula != null && celula.getCellType() == CellType.NUMERIC) {
                total++;
            }
        }
        return total;
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

    /** Normaliza o texto de cabeçalho: minúsculas, sem acentos. */
    private String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        String semAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos.toLowerCase(Locale.ROOT);
    }
}