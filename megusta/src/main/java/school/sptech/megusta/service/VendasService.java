package school.sptech.megusta.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.sptech.megusta.dto.planilha_vendas.BaixaInsumoResponse;
import school.sptech.megusta.dto.planilha_vendas.ItemVendido;
import school.sptech.megusta.exception.AcessoNegadoException;
import school.sptech.megusta.exception.EstoqueInsuficienteException;
import school.sptech.megusta.exception.PlanilhaInvalidaException;
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

    private final FogazzasRepository fogazzasRepository;
    private final FogazzaInsumoRepository fogazzaInsumoRepository;
    private final InsumoRepository insumoRepository;
    private final MotivoRepository motivoRepository;
    private final SaidaEstoqueRepository saidaEstoqueRepository;
    private final TipoStatusService tipoStatusService;
    private final PlanilhaVendasExtractor planilhaVendasExtractor;

    public VendasService(FogazzasRepository fogazzasRepository,
                         FogazzaInsumoRepository fogazzaInsumoRepository,
                         InsumoRepository insumoRepository,
                         MotivoRepository motivoRepository,
                         SaidaEstoqueRepository saidaEstoqueRepository,
                         TipoStatusService tipoStatusService,
                         PlanilhaVendasExtractor planilhaVendasExtractor) {
        this.fogazzasRepository = fogazzasRepository;
        this.fogazzaInsumoRepository = fogazzaInsumoRepository;
        this.insumoRepository = insumoRepository;
        this.motivoRepository = motivoRepository;
        this.saidaEstoqueRepository = saidaEstoqueRepository;
        this.tipoStatusService = tipoStatusService;
        this.planilhaVendasExtractor = planilhaVendasExtractor;
    }

    /**
     * Lê o relatório de itens vendidos da planilha. O arquivo só é aceito quando
     * alguma aba tem, no cabeçalho, as colunas {@code Nome Prod} e {@code Qtd.} —
     * qualquer outro layout é rejeitado com
     * {@link PlanilhaInvalidaException}, sem tocar no banco.
     *
     * @throws PlanilhaInvalidaException quando o arquivo não é o relatório de itens vendidos
     */
    public List<ItemVendido> lerPlanilha(MultipartFile planilha) throws IOException {
        try (InputStream inputStream = planilha.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            List<PlanilhaVendasExtractor.ColunasReconhecidas> reconhecidas =
                    planilhaVendasExtractor.reconhecer(workbook);
            if (reconhecidas.isEmpty()) {
                throw PlanilhaInvalidaException.paraArquivo(planilha.getOriginalFilename());
            }
            return planilhaVendasExtractor.extrair(workbook, reconhecidas);
        }
    }

    /**
     * Fluxo completo de importação: lê o relatório de itens vendidos e aplica a
     * baixa de estoque (UPDATE em {@code insumo} + INSERT em {@code saida_estoque})
     * em uma única transação.
     *
     * @return um registro por insumo cuja quantidade foi alterada, com o estoque
     *         antes da subtração, o consumo da importação e o saldo restante;
     *         lista vazia quando o relatório é válido mas nada é alterado
     */
    @Transactional
    public List<BaixaInsumoResponse> importarPlanilha(MultipartFile planilha) throws IOException {
        List<ItemVendido> itens = lerPlanilha(planilha);
        return baixarEstoque(itens);
    }

    /**
     * Baixa de estoque transacional: para cada item vendido, localiza a fogazza por
     * nome exato (case-insensitive), obtém os insumos consumidos via INNER JOIN em
     * {@code fogazza_insumo}, acumula {@code quantidade_insumo × unidades vendidas}
     * por insumo e, ao final, subtrai a quantidade atual, recalcula o status e grava
     * uma saída de estoque por insumo afetado. Itens sem fogazza cadastrada são
     * ignorados sem interromper a importação.
     *
     * @return um {@link BaixaInsumoResponse} por insumo alterado — um único registro
     *         por insumo, mesmo quando consumido por várias fogazzas
     */
    List<BaixaInsumoResponse> baixarEstoque(List<ItemVendido> itens) {
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

        List<BaixaInsumoResponse> baixas = new ArrayList<>();
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

            // Capturada antes do setQtdAtual, que sobrescreve o valor em memória
            BigDecimal quantidadeAtual = BigDecimal.valueOf(insumo.getQtdAtual());

            insumo.setQtdAtual(novaQuantidade);
            insumo.setTipoStatus(tipoStatusService.calcularStatusEstoque(novaQuantidade, insumo.getEstoqueMinimo()));
            insumoRepository.save(insumo);

            SaidaEstoque saida = new SaidaEstoque();
            saida.setInsumo(insumo);
            saida.setUsuario(usuario);
            saida.setMotivo(motivo);
            saida.setQuantidade(entrada.getValue());
            saidaEstoqueRepository.save(saida);

            baixas.add(new BaixaInsumoResponse(
                    insumo.getNome(),
                    insumo.getCodigoInsumo(),
                    insumo.getUnidadeMedida() == null ? null : insumo.getUnidadeMedida().getUnidade(),
                    quantidadeAtual,
                    entrada.getValue(),
                    BigDecimal.valueOf(novaQuantidade)));
        }
        return baixas;
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
}