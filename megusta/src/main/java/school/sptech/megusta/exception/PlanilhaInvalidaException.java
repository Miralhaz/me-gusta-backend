package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Arquivo enviado para importação que não corresponde ao relatório de itens
 * vendidos: falta a assinatura de colunas {@code Nome Prod} + {@code Qtd.}, o
 * arquivo não pôde ser lido como workbook ou está vazio.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PlanilhaInvalidaException extends RuntimeException {

    /** Colunas que definem o relatório de itens vendidos. */
    public static final String FORMATO_ESPERADO = "'Nome Prod' + 'Qtd.'";

    public PlanilhaInvalidaException(String message) {
        super(message);
    }

    /**
     * Cria a exceção nomeando o arquivo rejeitado e o formato esperado, para que
     * o operador saiba qual relatório exportar.
     *
     * @param nomeArquivo nome do arquivo enviado (pode ser {@code null})
     */
    public static PlanilhaInvalidaException paraArquivo(String nomeArquivo) {
        return new PlanilhaInvalidaException(String.format(
                "Planilha não reconhecida: o arquivo '%s' não é o relatório de itens vendidos. "
                        + "Envie o relatório exportado da plataforma, em .xlsx, com as colunas %s "
                        + "na linha de cabeçalho.",
                nomeArquivo == null ? "sem nome" : nomeArquivo, FORMATO_ESPERADO));
    }
}
