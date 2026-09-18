package school.sptech.megusta.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.sptech.megusta.dto.planilha_vendas.ItemVendido;
import school.sptech.megusta.exception.AcessoNegadoException;
import school.sptech.megusta.exception.EstoqueInsuficienteException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.FogazzaInsumo;
import school.sptech.megusta.model.Fogazzas;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.Motivo;
import school.sptech.megusta.model.SaidaEstoque;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.repository.FogazzaInsumoRepository;
import school.sptech.megusta.repository.FogazzasRepository;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.MotivoRepository;
import school.sptech.megusta.repository.SaidaEstoqueRepository;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VendasService {

    private static final String NOME_MOTIVO_VENDA = "Venda";

    private final DataFormatter dataFormatter = new DataFormatter();

    private final FogazzasRepository fogazzasRepository;
    private final FogazzaInsumoRepository fogazzaInsumoRepository;
    private final InsumoRepository insumoRepository;
    private final MotivoRepository motivoRepository;
    private final SaidaEstoqueRepository saidaEstoqueRepository;
    private final TipoStatusService tipoStatusService;

    public VendasService(FogazzasRepository fogazzasRepository,
                         FogazzaInsumoRepository fogazzaInsumoRepository,
                         InsumoRepository insumoRepository,
                         MotivoRepository motivoRepository,
                         SaidaEstoqueRepository saidaEstoqueRepository,
                         TipoStatusService tipoStatusService) {
        this.fogazzasRepository = fogazzasRepository;
        this.fogazzaInsumoRepository = fogazzaInsumoRepository;
        this.insumoRepository = insumoRepository;
        this.motivoRepository = motivoRepository;
        this.saidaEstoqueRepository = saidaEstoqueRepository;
        this.tipoStatusService = tipoStatusService;
    }

    /**
     * Lê a planilha de vendas, reconhece o formato e devolve os itens vendidos
     * extraídos, agregados por nome (soma das quantidades).
     */
    public List<ItemVendido> lerPlanilha(MultipartFile planilha) throws IOException {
        try (InputStream inputStream = planilha.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            return processarWorkbook(workbook);
        }
    }

    /**
     * Fluxo completo de importação: reconhece o formato da planilha, extrai os
     * itens vendidos e aplica a baixa de estoque (UPDATE em {@code insumo} +
     * INSERT em {@code saida_estoque}) em uma única transação.
     *
     * @return os itens extraídos da planilha, em formato JSON
     */
    @Transactional
    public List<ItemVendido> importarPlanilha(MultipartFile planilha) throws IOException {
        List<ItemVendido> itens = lerPlanilha(planilha);
        baixarEstoque(itens);
        return itens;
    }

    List<ItemVendido> processarWorkbook(Workbook workbook) {
        FormatoPlanilha formato = detectarFormato(workbook);
        List<ItemVendido> itens = switch (formato) {
            case HISTORICO_ITENS_VENDIDOS -> lerHistoricoItensVendidos(workbook.getSheetAt(0));
            case PEDIDOS_RECENTES -> lerPedidosRecentes(workbook.getSheetAt(0));
            case RELATORIO_CARDAPIO -> lerRelatorioCardapio(workbook);
        };
        return ItemVendido.agregarPorNome(itens);
    }

    FormatoPlanilha detectarFormato(Workbook workbook) {
        if (buscarAba(workbook, "Itens") != null && buscarAba(workbook, "Complementos") != null) {
            return FormatoPlanilha.RELATORIO_CARDAPIO;
        }
        Sheet primeiraAba = workbook.getSheetAt(0);
        if (indiceColuna(primeiraAba, "Itens") >= 0) {
            return FormatoPlanilha.PEDIDOS_RECENTES;
        }
        if (indiceColuna(primeiraAba, "Nome Prod") >= 0) {
            return FormatoPlanilha.HISTORICO_ITENS_VENDIDOS;
        }
        throw new IllegalArgumentException("Formato de planilha de vendas não suportado");
    }

    /**
     * Leitor do formato "histórico de itens vendidos": uma linha por item, com o
     * nome do produto em {@code Nome Prod} e a quantidade em {@code Qtd.}.
     * Linhas em branco, itens sem nome e quantidades não numéricas são ignorados.
     */
    List<ItemVendido> lerHistoricoItensVendidos(Sheet aba) {
        int colunaNome = indiceColuna(aba, "Nome Prod");
        int colunaQuantidade = indiceColuna(aba, "Qtd.");
        if (colunaNome < 0 || colunaQuantidade < 0) {
            return List.of();
        }

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

    /**
     * Leitor do formato "pedidos recentes": uma linha por pedido, com os nomes dos
     * itens separados por {@code ;} na coluna {@code Itens}. Cada ocorrência de um
     * nome conta como 1 unidade.
     */
    List<ItemVendido> lerPedidosRecentes(Sheet aba) {
        int colunaItens = indiceColuna(aba, "Itens");
        if (colunaItens < 0) {
            return List.of();
        }

        List<ItemVendido> itens = new ArrayList<>();
        for (int i = 1; i <= aba.getLastRowNum(); i++) {
            Row linha = aba.getRow(i);
            if (linha == null) {
                continue;
            }
            String celula = valorTexto(linha.getCell(colunaItens));
            if (celula == null || celula.isBlank()) {
                continue;
            }
            String[] nomes = celula.split(";");
            for (String nome : nomes) {
                String nomeLimpo = nome.trim();
                if (!nomeLimpo.isEmpty()) {
                    itens.add(new ItemVendido(nomeLimpo, BigDecimal.ONE));
                }
            }
        }
        return itens;
    }

    /**
     * Leitor do formato "relatório de cardápio": abas {@code Itens} e
     * {@code Complementos}, cada uma com nome do item e quantidade vendida em
     * colunas próprias. Os pares de ambas as abas são somados por nome.
     */
    List<ItemVendido> lerRelatorioCardapio(Workbook workbook) {
        List<ItemVendido> itens = new ArrayList<>();
        itens.addAll(lerAbaRelatorioCardapio(workbook, "Itens", "Nome do item", "Vendas total (quantidade)"));
        itens.addAll(lerAbaRelatorioCardapio(workbook, "Complementos", "Nome do complemento", "Vendas Total (Quantidade)"));
        return itens;
    }

    /**
     * Baixa de estoque transacional: para cada item vendido, localiza a fogazza por
     * nome exato (case-insensitive), obtém os insumos consumidos via INNER JOIN em
     * {@code fogazza_insumo}, acumula {@code quantidade_insumo × unidades vendidas}
     * por insumo e, ao final, subtrai a quantidade atual, recalcula o status e grava
     * uma saída de estoque por insumo afetado. Itens sem fogazza cadastrada são
     * ignorados sem interromper a importação.
     */
    void baixarEstoque(List<ItemVendido> itens) {
        Usuario usuario = obterUsuarioAutenticado();
        Motivo motivo = obterOuCriarMotivoVenda();

        Map<Integer, BigDecimal> totaisPorInsumo = new LinkedHashMap<>();
        for (ItemVendido item : itens) {
            if (item.getNomeItem() == null || item.getNomeItem().isBlank()) {
                continue;
            }

            Optional<Fogazzas> fogazzaEncontrada = fogazzasRepository.findByNomeIgnoreCase(item.getNomeItem());
            if (fogazzaEncontrada.isEmpty()) {
                continue;
            }

            List<FogazzaInsumo> receita = fogazzaInsumoRepository.findByFogazzaId(fogazzaEncontrada.get().getId());
            for (FogazzaInsumo registro : receita) {
                BigDecimal quantidadePorUnidade = registro.getQuantidadeInsumo() == null
                        ? BigDecimal.ZERO : registro.getQuantidadeInsumo();
                BigDecimal unidadesVendidas = item.getQuantidade() == null
                        ? BigDecimal.ZERO : item.getQuantidade();
                BigDecimal total = quantidadePorUnidade.multiply(unidadesVendidas);
                totaisPorInsumo.merge(registro.getInsumo().getId(), total, BigDecimal::add);
            }
        }

        for (Map.Entry<Integer, BigDecimal> entrada : totaisPorInsumo.entrySet()) {
            Insumo insumo = insumoRepository.findById(entrada.getKey())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo não encontrado."));

            double consumo = entrada.getValue().doubleValue();
            double novaQuantidade = insumo.getQtdAtual() - consumo;
            if (novaQuantidade < 0) {
                throw new EstoqueInsuficienteException(String.format(
                        "Estoque insuficiente do insumo '%s' (%s). Disponível: %s, necessário: %s.",
                        insumo.getNome(), insumo.getCodigoInsumo(), insumo.getQtdAtual(), consumo));
            }
            insumo.setQtdAtual(novaQuantidade);
            insumo.setTipoStatus(tipoStatusService.calcularStatusEstoque(novaQuantidade, insumo.getEstoqueMinimo()));
            insumoRepository.save(insumo);

            SaidaEstoque saida = new SaidaEstoque();
            saida.setInsumo(insumo);
            saida.setUsuario(usuario);
            saida.setMotivo(motivo);
            saida.setQuantidade(entrada.getValue());
            saidaEstoqueRepository.save(saida);
        }
    }

    private Usuario obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Usuario usuario) {
            return usuario;
        }
        throw new AcessoNegadoException("Usuário não autenticado.");
    }

    private Motivo obterOuCriarMotivoVenda() {
        return motivoRepository.findByNomeIgnoreCase(NOME_MOTIVO_VENDA)
                .orElseGet(() -> {
                    Motivo motivo = new Motivo();
                    motivo.setNome(NOME_MOTIVO_VENDA);
                    return motivoRepository.save(motivo);
                });
    }

    private List<ItemVendido> lerAbaRelatorioCardapio(Workbook workbook, String nomeAba,
                                                      String nomeColuna, String colunaQuantidade) {
        Sheet aba = buscarAba(workbook, nomeAba);
        if (aba == null) {
            return List.of();
        }
        int colunaNome = indiceColuna(aba, nomeColuna);
        int colunaQtd = indiceColuna(aba, colunaQuantidade);
        if (colunaNome < 0 || colunaQtd < 0) {
            return List.of();
        }

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
            BigDecimal quantidade = valorQuantidade(linha.getCell(colunaQtd));
            if (quantidade == null || quantidade.signum() <= 0) {
                continue;
            }
            itens.add(new ItemVendido(nome, quantidade));
        }
        return itens;
    }

    private Sheet buscarAba(Workbook workbook, String nomeAba) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            if (workbook.getSheetName(i).equalsIgnoreCase(nomeAba)) {
                return workbook.getSheetAt(i);
            }
        }
        return null;
    }

    private int indiceColuna(Sheet aba, String nomeColuna) {
        Row cabecalho = aba.getRow(0);
        if (cabecalho == null) {
            return -1;
        }
        for (Cell celula : cabecalho) {
            String valor = valorTexto(celula);
            if (valor != null && valor.equalsIgnoreCase(nomeColuna)) {
                return celula.getColumnIndex();
            }
        }
        return -1;
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

    enum FormatoPlanilha {
        HISTORICO_ITENS_VENDIDOS,
        PEDIDOS_RECENTES,
        RELATORIO_CARDAPIO
    }
}