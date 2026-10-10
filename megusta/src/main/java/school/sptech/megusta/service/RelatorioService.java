package school.sptech.megusta.service;

import org.springframework.stereotype.Service;

import school.sptech.megusta.dto.consumo_geral_categoria.ConsumoGeralCategoriaResponseDto;
import school.sptech.megusta.dto.consumo_intermediario_categoria.ConsumoIntermediarioCategoriaResponseDto;
import school.sptech.megusta.dto.entrada_estoque.EntradaEstoqueResponse;
import school.sptech.megusta.dto.insumo.InsumoResponse;
import school.sptech.megusta.dto.relatorio.*;
//import school.sptech.megusta.dto.*; essa linha impede que a aplicação suba no intellij não sei porque
import school.sptech.megusta.dto.saida_estoque.SaidaEstoqueResponse;

import school.sptech.megusta.mapper.EntradaEstoqueMapper;
import school.sptech.megusta.mapper.InsumoMapper;
import school.sptech.megusta.mapper.SaidaEstoqueMapper;

import school.sptech.megusta.model.EntradaEstoque;
import school.sptech.megusta.model.Insumo;

import school.sptech.megusta.repository.CategoriaInsumoRepository;
import school.sptech.megusta.repository.EntradaEstoqueRepository;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.SaidaEstoqueRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final CategoriaInsumoRepository categoriaInsumoRepository;
    private final EntradaEstoqueRepository entradaEstoqueRepository;
    private final InsumoRepository insumoRepository;
    private final SaidaEstoqueRepository saidaEstoqueRepository;

    private static final Set<String> MOTIVOS_PERDA = Set.of("vencimento", "vencido", "descarte", "quebra", "perda");

    public RelatorioService(CategoriaInsumoRepository categoriaInsumoRepository, EntradaEstoqueRepository entradaEstoqueRepository, InsumoRepository insumoRepository, SaidaEstoqueRepository saidaEstoqueRepository) {
        this.categoriaInsumoRepository = categoriaInsumoRepository;
        this.entradaEstoqueRepository = entradaEstoqueRepository;
        this.insumoRepository = insumoRepository;
        this.saidaEstoqueRepository = saidaEstoqueRepository;
    }

    // GERAÇÃO DO RELATÓRIO
    public RelatorioResponseDto gerarRelatorio(RelatorioRequestDto request) {

        validarRequest(request);

        LocalDateTime inicio = request.getDataInicio().atStartOfDay();
        LocalDateTime fim = request.getDataFim().plusDays(1).atStartOfDay().minusNanos(1);

        RelatorioResponseDto response = new RelatorioResponseDto();

        response.setTitulo(gerarTitulo(request));
        response.setDataInicio(request.getDataInicio());
        response.setDataFim(request.getDataFim());

        //CONSUMO
        if (possuiItem(request, TipoItemRelatorio.INSUMOS_MAIS_UTILIZADOS)) {
            carregarInsumosMaisUtilizados(response, inicio, fim);
        }

        if (possuiItem(request, TipoItemRelatorio.QUANTIDADE_TOTAL_CONSUMIDA_POR_INSUMO)) {
            carregarConsumoPorInsumo(response, inicio, fim);
        }

        if (possuiItem(request, TipoItemRelatorio.CONSUMO_POR_CATEGORIA)) {
            carregarConsumoPorCategoria(response, inicio, fim);
        }

        if (possuiItem(request, TipoItemRelatorio.MEDIA_CONSUMO)) {
            carregarMediaConsumo(response, inicio, fim);
        }

        //SAÍDAS
        if (possuiItem(request, TipoItemRelatorio.SAIDAS_POR_PERIODO)) {
            carregarSaidas(response, inicio, fim);
        }

        if (possuiItem(request, TipoItemRelatorio.MOTIVOS_SAIDAS)) {
            carregarMotivosSaidas(response, inicio, fim);
        }

        if (possuiItem(request, TipoItemRelatorio.PERDAS)) {
            carregarPerdas(response, inicio, fim);
        }

        // ENTRADAS
        if (possuiItem(request, TipoItemRelatorio.ENTRADAS_POR_PERIODO)) {
            carregarEntradas(response, inicio, fim);
        }

        if (possuiItem(request, TipoItemRelatorio.VALOR_TOTAL_ENTRADAS)) {
            carregarValorTotalEntradas(response, inicio, fim);
        }

        if (possuiItem(request, TipoItemRelatorio.FORNECEDORES_MAIS_ABASTECERAM)) {
            carregarFornecedores(response, inicio, fim);
        }

        //VALIDADE
          if (possuiItem(request, TipoItemRelatorio.ITENS_PROXIMOS_VENCIMENTO)) {
            carregarProximosVencimento(response, request.getDataInicio(), request.getDataFim());
        }

        if (possuiItem(request, TipoItemRelatorio.ITENS_VENCIDOS)) {
            carregarVencidos(response);
        }

        // ESTOQUE
        if (possuiItem(request, TipoItemRelatorio.ESTOQUE_ATUAL)) {
            carregarEstoqueAtual(response);
        }

        if (possuiItem(request, TipoItemRelatorio.ITENS_ABAIXO_ESTOQUE_MINIMO)) {
            carregarAbaixoEstoqueMinimo(response);
        }

        //COMPARATIVO
        if (possuiItem(request, TipoItemRelatorio.ENTRADA_SAIDA_INSUMO)) {
            carregarEntradaSaidaInsumo(response, request, inicio, fim);
        }

        return response;
    }

    // ENTRADAS
    private void carregarEntradas(RelatorioResponseDto response, LocalDateTime inicio, LocalDateTime fim) {

        List<EntradaEstoque> entradas = entradaEstoqueRepository.findByDtEntradaBetween(inicio, fim);
        List<EntradaEstoqueResponse> dto = EntradaEstoqueMapper.toResponse(entradas);

        response.setEntradas(dto);
    }

    private void carregarValorTotalEntradas(RelatorioResponseDto response, LocalDateTime inicio, LocalDateTime fim) {

        BigDecimal valor = entradaEstoqueRepository.buscarValorTotalEntradas(inicio, fim);

        response.setValorTotalEntradas(valor == null ? BigDecimal.ZERO : valor);
    }

    private void carregarFornecedores(RelatorioResponseDto response, LocalDateTime inicio, LocalDateTime fim) {

        List<EntradaEstoque> entradas = entradaEstoqueRepository.findByDtEntradaBetween(inicio, fim);

        Map<Integer, List<EntradaEstoque>> agrupadas = entradas.stream().filter(e -> e.getFornecedor() != null)
                        .collect(Collectors.groupingBy(e -> e.getFornecedor().getId()));

        List<FornecedorAbastecimentoRelatorioDto> fornecedores = new ArrayList<>();

        for (Map.Entry<Integer, List<EntradaEstoque>> entry : agrupadas.entrySet()) {

            List<EntradaEstoque> entradasFornecedor = entry.getValue();

            if (entradasFornecedor.isEmpty()) {
                continue;
            }

            EntradaEstoque primeira = entradasFornecedor.get(0);

            BigDecimal valorTotal = entradasFornecedor.stream().map(EntradaEstoque::getVlTotal)
                            .filter(valor -> valor != null).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal quantidadeEntradas = BigDecimal.valueOf(entradasFornecedor.size());

            FornecedorAbastecimentoRelatorioDto dto = new FornecedorAbastecimentoRelatorioDto();

            dto.setFornecedorId(primeira.getFornecedor().getId());
            dto.setNomeFornecedor(primeira.getFornecedor().getNome());
            dto.setQuantidadeTotal(quantidadeEntradas);
            dto.setValorTotal(valorTotal);

            fornecedores.add(dto);
        }

        fornecedores.sort(Comparator.comparing(FornecedorAbastecimentoRelatorioDto::getQuantidadeTotal).reversed());

        response.setFornecedores(fornecedores);
    }

    // SAÍDAS
    private void carregarSaidas(RelatorioResponseDto response, LocalDateTime inicio, LocalDateTime fim) {

        var saidas = saidaEstoqueRepository.findByDtSaidaBetween(inicio, fim);

        List<SaidaEstoqueResponse> dto = SaidaEstoqueMapper.toResponse(saidas);

        response.setSaidas(dto);
    }

    private void carregarMotivosSaidas(RelatorioResponseDto response, LocalDateTime inicio, LocalDateTime fim) {

        List<Object[]> resultados = saidaEstoqueRepository.somarSaidasPorMotivo(inicio, fim);

        List<MotivoSaidaRelatorioDto> motivos = resultados.stream().map(resultado -> {

                            MotivoSaidaRelatorioDto dto = new MotivoSaidaRelatorioDto();
                            dto.setMotivo((String) resultado[0]);
                            dto.setQuantidadeTotal(toBigDecimal(resultado[1]));

                            return dto;
                        }).toList();

        response.setMotivosSaida(motivos);
    }

    private void carregarPerdas(RelatorioResponseDto response, LocalDateTime inicio, LocalDateTime fim) {

        List<Object[]> resultados = saidaEstoqueRepository.somarSaidasPorMotivo(inicio, fim);
        List<MotivoSaidaRelatorioDto> perdas = resultados.stream()
                        .filter(resultado -> {

                            String motivo = (String) resultado[0];

                            return ehMotivoPerda(motivo);
                        })
                        .map(resultado -> {

                            MotivoSaidaRelatorioDto dto = new MotivoSaidaRelatorioDto();

                            dto.setMotivo((String) resultado[0]);
                            dto.setQuantidadeTotal(toBigDecimal(resultado[1]));

                            return dto;
                        }).toList();

        response.setPerdas(perdas);
    }

    // CONSUMO POR INSUMO
    private void carregarInsumosMaisUtilizados(RelatorioResponseDto response, LocalDateTime inicio, LocalDateTime fim) {

        List<ConsumoInsumoRelatorioDto> consumos = buscarConsumoPorInsumos(inicio, fim);

        response.setInsumosMaisUtilizados(consumos);
    }

    private void carregarConsumoPorInsumo(RelatorioResponseDto response, LocalDateTime inicio, LocalDateTime fim) {

        response.setConsumoPorInsumo(buscarConsumoPorInsumos(inicio, fim));
    }

    private List<ConsumoInsumoRelatorioDto> buscarConsumoPorInsumos(LocalDateTime inicio, LocalDateTime fim) {

        List<Object[]> resultados = saidaEstoqueRepository
                        .buscarInsumosMaisUtilizados(inicio, fim);

        Map<String, Insumo> insumosPorNome = insumoRepository.findAll().stream()
                        .collect(Collectors.toMap(Insumo::getNome, insumo -> insumo, (primeiro, segundo) -> primeiro));

        return resultados.stream().map(resultado -> {

                    String nome = (String) resultado[0];
                    BigDecimal quantidade = toBigDecimal(resultado[1]);
                    Insumo insumo = insumosPorNome.get(nome);
                    ConsumoInsumoRelatorioDto dto = new ConsumoInsumoRelatorioDto();

                    if (insumo != null) {

                        dto.setInsumoId(insumo.getId());

                        if (insumo.getUnidadeMedida() != null) {
                            dto.setUnidadeMedida(insumo.getUnidadeMedida().getUnidade());
                        }
                    }

                    dto.setNomeInsumo(nome);
                    dto.setQuantidadeConsumida(quantidade);

                    return dto;}).toList();
    }

    // CONSUMO POR CATEGORIA
    private void carregarConsumoPorCategoria(RelatorioResponseDto response, LocalDateTime inicio, LocalDateTime fim) {

        List<ConsumoIntermediarioCategoriaResponseDto> consumos = categoriaInsumoRepository
                        .consumoPorTodasAsCategoriasNoPeriodo(inicio, fim);


        Map<String, List<ConsumoIntermediarioCategoriaResponseDto>> agrupado = consumos.stream().collect(Collectors.groupingBy(
                        ConsumoIntermediarioCategoriaResponseDto::getNomeCategoria, LinkedHashMap::new, Collectors.toList()));
        List<ConsumoGeralCategoriaResponseDto> resposta = agrupado.entrySet().stream().map(entry ->
                                new ConsumoGeralCategoriaResponseDto(entry.getKey(), entry.getValue())).toList();

        response.setConsumoPorCategoria(resposta
        );
    }

    // MÉDIA DE CONSUMO
    private void carregarMediaConsumo(RelatorioResponseDto response, LocalDateTime inicio, LocalDateTime fim) {
        List<Insumo> insumos = insumoRepository.findAll();
        List<MediaConsumoRelatorioDto> medias = new ArrayList<>();

        for (Insumo insumo : insumos) {

            if (!insumo.isAtivo()) {
                continue;
            }

            BigDecimal media = insumoRepository.mediaConsumoDiarioPorInsumo(insumo.getId(), inicio, fim);

            MediaConsumoRelatorioDto dto = new MediaConsumoRelatorioDto();

            dto.setInsumoId(insumo.getId());
            dto.setNomeInsumo(insumo.getNome());
            dto.setMediaDiaria(media == null ? BigDecimal.ZERO : media);

            if (insumo.getUnidadeMedida() != null) {
                dto.setUnidadeMedida(insumo.getUnidadeMedida().getUnidade());
            }
            medias.add(dto);
        }
        medias.sort(Comparator.comparing(MediaConsumoRelatorioDto::getMediaDiaria).reversed());

        response.setMediaConsumo(medias);
    }

    // VALIDADE
    private void carregarProximosVencimento(RelatorioResponseDto response, LocalDate dataInicio, LocalDate dataFim) {

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = dataInicio.isBefore(hoje) ? hoje : dataInicio;

        if (inicio.isAfter(dataFim)) {
            response.setProximosVencimento(List.of());

            return;
        }

        List<EntradaEstoque> entradas = entradaEstoqueRepository.buscarVencimentosEntre(inicio, dataFim);

        response.setProximosVencimento(EntradaEstoqueMapper.toResponse(entradas));
    }

    private void carregarVencidos(RelatorioResponseDto response) {

        LocalDate hoje = LocalDate.now();

        List<EntradaEstoque> vencidos = entradaEstoqueRepository.findAll().stream()
                        .filter(entrada -> entrada.getDtValidade() != null)
                        .filter(entrada -> entrada.getDtValidade().isBefore(hoje))
                        .sorted(Comparator.comparing(EntradaEstoque::getDtValidade)).toList();

        response.setVencidos(EntradaEstoqueMapper.toResponse(vencidos)
        );
    }

    // ESTOQUE
    private void carregarEstoqueAtual(RelatorioResponseDto response) {

        List<Insumo> insumos = insumoRepository.findAll().stream().filter(Insumo::isAtivo).toList();
        List<InsumoResponse> dto = InsumoMapper.toResponse(insumos);

        response.setEstoqueAtual(dto);
    }

    private void carregarAbaixoEstoqueMinimo(RelatorioResponseDto response) {

        List<Insumo> insumos = insumoRepository.buscarAbaixoDoEstoqueMinimo();

        response.setAbaixoEstoqueMinimo(InsumoMapper.toResponse(insumos));
    }

    // COMPARAÇÃO ENTRADA X SAÍDA
    private void carregarEntradaSaidaInsumo(RelatorioResponseDto response, RelatorioRequestDto request, LocalDateTime inicio, LocalDateTime fim) {

        if (request.getInsumoId() == null) {
            throw new IllegalArgumentException("Para gerar a relação entre entrada e saída, é necessário informar o insumoId.");
        }

        Insumo insumo = insumoRepository.findById(request.getInsumoId())
                .orElseThrow(() -> new IllegalArgumentException("Insumo não encontrado."));

        BigDecimal totalEntradas = entradaEstoqueRepository.sumQuantidadeTotalEntradaByInsumoAndDateBetween(insumo.getId(), inicio, fim);
        BigDecimal totalSaidas = saidaEstoqueRepository.sumQuantidadeSaidasByInsumoAndDateBetween(insumo.getId(), inicio, fim);

        if (totalEntradas == null) {
            totalEntradas = BigDecimal.ZERO;
        }

        if (totalSaidas == null) {
            totalSaidas = BigDecimal.ZERO;
        }

        EntradaSaidaInsumoRelatorioDto dto = new EntradaSaidaInsumoRelatorioDto();

        dto.setInsumoId(insumo.getId());
        dto.setNomeInsumo(insumo.getNome());
        dto.setTotalEntradas(totalEntradas);
        dto.setTotalSaidas(totalSaidas);
        dto.setDiferenca(totalEntradas.subtract(totalSaidas));

        if (insumo.getUnidadeMedida() != null) {
            dto.setUnidadeMedida(insumo.getUnidadeMedida().getUnidade());
        }

        response.setEntradaSaidaInsumo(dto);
    }

    // PDF
    public byte[] gerarPdf(RelatorioRequestDto request) {

        RelatorioResponseDto relatorio = gerarRelatorio(request);

        String corpo = montarCorpoPdf(relatorio);

        return gerarPdfBytes(relatorio.getTitulo(), corpo);
    }

    private String montarCorpoPdf(RelatorioResponseDto relatorio) {

        StringBuilder corpo = new StringBuilder();

        DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        corpo.append("Período: ")
                .append(relatorio.getDataInicio().format(dataFormatter))
                .append(" até ")
                .append(relatorio.getDataFim().format(dataFormatter)
                ).append("\n");

        corpo.append("Gerado em: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .append("\n");

        // CONSUMO
        if (relatorio.getInsumosMaisUtilizados() != null) {

            corpo.append("\nINSUMOS MAIS UTILIZADOS\n");

            int posicao = 1;

            for (ConsumoInsumoRelatorioDto item : relatorio.getInsumosMaisUtilizados()) {

                corpo.append(posicao++)
                        .append(". ")
                        .append(item.getNomeInsumo())
                        .append(" - ")
                        .append(formatarNumero(item.getQuantidadeConsumida()));

                adicionarUnidade(corpo, item.getUnidadeMedida());

                corpo.append("\n");
            }
        }

        if (relatorio.getConsumoPorInsumo() != null) {

            corpo.append("\nQUANTIDADE TOTAL CONSUMIDA POR INSUMO\n");

            for (ConsumoInsumoRelatorioDto item : relatorio.getConsumoPorInsumo()) {

                corpo.append(item.getNomeInsumo())
                        .append(" - ")
                        .append(formatarNumero(item.getQuantidadeConsumida()));

                adicionarUnidade(corpo, item.getUnidadeMedida());

                corpo.append("\n");
            }
        }

        if (relatorio.getConsumoPorCategoria() != null) {

            corpo.append("\nCONSUMO POR CATEGORIA\n");

            for (ConsumoGeralCategoriaResponseDto categoria : relatorio.getConsumoPorCategoria()) {

                corpo.append(categoria.getNomeCategoria()).append("\n");

                for (ConsumoIntermediarioCategoriaResponseDto consumo : categoria.getConsumos()) {

                    corpo.append("  ")
                            .append(consumo.getDtConsumo().format(dataFormatter))
                            .append(" - ")
                            .append(formatarNumero(consumo.getQuantidade()))
                            .append("\n");
                }
            }
        }

        if (relatorio.getMediaConsumo() != null) {

            corpo.append("\nMÉDIA DE CONSUMO\n");

            for (MediaConsumoRelatorioDto item : relatorio.getMediaConsumo()) {

                corpo.append(item.getNomeInsumo()).append(" - ")
                        .append(formatarNumero(item.getMediaDiaria()));

                adicionarUnidade(corpo, item.getUnidadeMedida());

                corpo.append("/dia\n");
            }
        }

        // SAÍDAS
        if (relatorio.getSaidas() != null) {

            corpo.append("\nSAÍDAS NO PERÍODO\n");

            for (SaidaEstoqueResponse saida : relatorio.getSaidas()) {

                corpo.append(saida.getInsumo().getNome())
                        .append(" - ")
                        .append(formatarNumero(saida.getQuantidade()));

                if (saida.getMotivo() != null) {

                    corpo.append(" - Motivo: ")
                            .append(saida.getMotivo().getNome());
                }

                corpo.append("\n");
            }
        }

        if (relatorio.getMotivosSaida() != null) {

            corpo.append("\nMOTIVOS DAS SAÍDAS\n");

            for (MotivoSaidaRelatorioDto motivo : relatorio.getMotivosSaida()) {

                corpo.append(motivo.getMotivo())
                        .append(" - ")
                        .append(formatarNumero(motivo.getQuantidadeTotal()))
                        .append("\n");
            }
        }

        if (relatorio.getPerdas() != null) {

            corpo.append("\nPERDAS\n");

            for (MotivoSaidaRelatorioDto perda : relatorio.getPerdas()) {

                corpo.append(perda.getMotivo())
                        .append(" - ")
                        .append(formatarNumero(perda.getQuantidadeTotal()))
                        .append("\n");
            }
        }

        // ENTRADAS
        if (relatorio.getEntradas() != null) {

            corpo.append("\nENTRADAS NO PERÍODO\n");

            for (EntradaEstoqueResponse entrada : relatorio.getEntradas()) {

                corpo.append(entrada.getInsumo().getNome())
                        .append(" - ")
                        .append(formatarNumero(calcularQuantidadeEntrada(entrada)));

                if (entrada.getUnidadeMedida() != null) {
                    adicionarUnidade(corpo, entrada.getUnidadeMedida().getUnidade());
                }

                if (entrada.getFornecedor() != null) {

                    corpo.append(" - ")
                            .append(entrada.getFornecedor().getNome());
                }

                corpo.append("\n");
            }
        }

        if (relatorio.getValorTotalEntradas() != null) {

            corpo.append("\nVALOR TOTAL DAS ENTRADAS\nR$ ")
                    .append(relatorio.getValorTotalEntradas().setScale(2, RoundingMode.HALF_UP))
                    .append("\n");
        }

        if (relatorio.getFornecedores() != null) {

            corpo.append("\nFORNECEDORES QUE MAIS ABASTECERAM\n");

            for (FornecedorAbastecimentoRelatorioDto fornecedor : relatorio.getFornecedores()) {
                corpo.append(fornecedor.getNomeFornecedor())
                        .append(" - Entradas: ")
                        .append(fornecedor.getQuantidadeTotal())
                        .append(" - Valor: R$ ")
                        .append(fornecedor.getValorTotal().setScale(2, RoundingMode.HALF_UP))
                        .append("\n");
            }
        }

        // VALIDADE
        if (relatorio.getProximosVencimento() != null) {
            corpo.append("\nITENS PRÓXIMOS DO VENCIMENTO\n");
            adicionarEntradasValidade(corpo, relatorio.getProximosVencimento(), dataFormatter);
        }

        if (relatorio.getVencidos() != null) {

            corpo.append("\nITENS VENCIDOS\n");

            adicionarEntradasValidade(corpo, relatorio.getVencidos(), dataFormatter);
        }

        // ESTOQUE
        if (relatorio.getEstoqueAtual() != null) {
            corpo.append("\nESTOQUE ATUAL\n");

            for (InsumoResponse insumo : relatorio.getEstoqueAtual()) {

                corpo.append(insumo.getNome()).append(" - ").append(insumo.getQuantidadeAtual());

                if (insumo.getUnidadeInsumo() != null) {
                    adicionarUnidade(corpo, insumo.getUnidadeInsumo().getUnidade());
                }

                corpo.append("\n");
            }
        }

        if (relatorio.getAbaixoEstoqueMinimo() != null) {

            corpo.append("\nITENS ABAIXO DO ESTOQUE MÍNIMO\n");

            for (InsumoResponse insumo : relatorio.getAbaixoEstoqueMinimo()) {

                corpo.append(insumo.getNome()).append(" - Atual: ").append(insumo.getQuantidadeAtual()).append(" - Mínimo: ").append(insumo.getEstoqueMinimo()).append("\n");
            }
        }

        // ENTRADA X SAÍDA
        if (relatorio.getEntradaSaidaInsumo() != null) {

            EntradaSaidaInsumoRelatorioDto item = relatorio.getEntradaSaidaInsumo();

            corpo.append("\nRELAÇÃO ENTRE ENTRADA E SAÍDA\n");
            corpo.append("Insumo: ").append(item.getNomeInsumo()).append("\n");
            corpo.append("Entradas: ").append(formatarNumero(item.getTotalEntradas()));

            adicionarUnidade(corpo, item.getUnidadeMedida());

            corpo.append("\n");
            corpo.append("Saídas: ").append(formatarNumero(item.getTotalSaidas()));

            adicionarUnidade(corpo, item.getUnidadeMedida());

            corpo.append("\n");
            corpo.append("Diferença: ").append(formatarNumero(item.getDiferenca()));

            adicionarUnidade(corpo, item.getUnidadeMedida());

            corpo.append("\n");
        }

        return corpo.toString();
    }

    // TÍTULO
    private String gerarTitulo(RelatorioRequestDto request) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (request.getTipo() == null) {
            return "Relatório - " + request.getDataInicio().format(formatter) + " a " + request.getDataFim().format(formatter);
        }

        String tipo = request.getTipo().trim().toUpperCase();

        return switch (tipo) {

            case "DIARIO" ->
                    "Relatório - " + request.getDataInicio().format(formatter);

            case "SEMANAL" ->
                    "Relatório - Semana " + request.getDataInicio().format(formatter) + " a " + request.getDataFim().format(formatter);

            case "MENSAL" -> {

                String mes = request.getDataInicio().getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));

                mes = mes.substring(0, 1).toUpperCase() + mes.substring(1);

                yield "Relatório - " + mes + "/" + request.getDataInicio().getYear();
            }

            default -> "Relatório - " + request.getDataInicio().format(formatter) + " a " + request.getDataFim().format(formatter);
        };
    }

    // VALIDAÇÃO
    private void validarRequest(RelatorioRequestDto request) {

        if (request == null) {
            throw new IllegalArgumentException("Os dados do relatório são obrigatórios.");
        }

        if (request.getDataInicio() == null || request.getDataFim() == null) {
            throw new IllegalArgumentException("A data inicial e a data final são obrigatórias.");
        }

        if (request.getDataInicio().isAfter(request.getDataFim())) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        }

        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new IllegalArgumentException("Selecione pelo menos um item para o relatório.");
        }
    }

    private boolean possuiItem(RelatorioRequestDto request, TipoItemRelatorio item) {
        return request.getItens().contains(item);
    }

    // HELPERS
    private boolean ehMotivoPerda(String motivo) {

        if (motivo == null || motivo.isBlank() ) {
            return false;
        }

        String normalizado = motivo.trim().toLowerCase();

        return MOTIVOS_PERDA.stream().anyMatch(normalizado::contains);
    }

    private BigDecimal toBigDecimal(Object valor) {

        return switch (valor) {
            case null -> BigDecimal.ZERO;
            case BigDecimal bigDecimal -> bigDecimal;
            case Number numero -> BigDecimal.valueOf(numero.doubleValue());
            default -> new BigDecimal(valor.toString());
        };

    }

    private BigDecimal calcularQuantidadeEntrada(EntradaEstoqueResponse entrada) {

        BigDecimal absoluta = entrada.getQuantidadeAbsoluta() == null ? BigDecimal.ZERO : entrada.getQuantidadeAbsoluta();
        BigDecimal relativa = entrada.getQuantidadeRelativa() == null ? BigDecimal.ONE : entrada.getQuantidadeRelativa();

        return absoluta.multiply(relativa);
    }

    private void adicionarUnidade(StringBuilder corpo, String unidade) {

        if (unidade != null && !unidade.isBlank()) {
            corpo.append(" ").append(unidade);
        }
    }

    private void adicionarEntradasValidade(StringBuilder corpo, List<EntradaEstoqueResponse> entradas, DateTimeFormatter formatter) {

        for (EntradaEstoqueResponse entrada : entradas) {

            corpo.append(entrada.getInsumo().getNome());

            if (entrada.getLote() != null) {
                corpo.append(" - Lote: ").append(entrada.getLote());
            }

            if (entrada.getDtValidade() != null) {
                corpo.append(" - Validade: ").append(entrada.getDtValidade().format(formatter));
            }

            corpo.append("\n");
        }
    }

    private String formatarNumero(BigDecimal valor) {

        if (valor == null) {
            return "0";
        }

        return valor.stripTrailingZeros().toPlainString();
    }

    // GERADOR PDF
    private byte[] gerarPdfBytes(String titulo, String corpo) {

        String[] linhas = corpo.split("\\n");

        StringBuilder stream = new StringBuilder();

        stream.append("BT\n");
        stream.append("/F1 20 Tf\n");
        stream.append("72 790 Td\n");
        stream.append("(").append(escapePdfText(titulo)).append(") Tj\n");
        stream.append("0 -30 Td\n");
        stream.append("/F1 12 Tf\n");

        for (String linha : linhas) {
            stream.append("(").append(escapePdfText(linha)).append(") Tj\n");stream.append("0 -18 Td\n");
        }

        stream.append("ET");

        String streamText = stream.toString();

        byte[] streamBytes = streamText.getBytes(StandardCharsets.ISO_8859_1);

        List<String> objects = new ArrayList<>();

        objects.add("<< /Type /Catalog /Pages 2 0 R >>");
        objects.add("<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
        objects.add("<< /Type /Page /Parent 2 0 R " + "/MediaBox [0 0 595 842] " + "/Contents 4 0 R " + "/Resources << /Font << /F1 5 0 R >> >> >>");
        objects.add("<< /Length " + streamBytes.length + " >>\nstream\n" + streamText + "\nendstream");
        objects.add("<< /Type /Font /Subtype /Type1 " + "/BaseFont /Helvetica >>");

        StringBuilder pdf = new StringBuilder();

        pdf.append("%PDF-1.4\n");

        List<Integer> offsets = new ArrayList<>();

        for (int i = 0; i < objects.size(); i++) {

            offsets.add(pdf.length());
            pdf.append(i + 1).append(" 0 obj\n");
            pdf.append(objects.get(i)).append("\nendobj\n");
        }

        int xrefStart = pdf.length();

        pdf.append("xref\n");
        pdf.append("0 ").append(objects.size() + 1).append("\n");
        pdf.append("0000000000 65535 f \n");

        for (Integer offset : offsets) {
            pdf.append(String.format("%010d 00000 n \n", offset));
        }

        pdf.append("trailer\n");
        pdf.append("<< /Size ").append(objects.size() + 1).append(" /Root 1 0 R >>\n");
        pdf.append("startxref\n");
        pdf.append(xrefStart).append("\n");
        pdf.append("%%EOF");

        return pdf.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private String escapePdfText(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\n", "\\n");
    }
}