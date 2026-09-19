package school.sptech.megusta.service;

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
     * Lê a planilha de vendas de forma auto-adaptativa: o extrator identifica
     * por heurística as colunas de nome e de quantidade em cada aba (qualquer
     * layout) e devolve os itens vendidos extraídos, agregados por nome (soma
     * das quantidades).
     */
    public List<ItemVendido> lerPlanilha(MultipartFile planilha) throws IOException {
        try (InputStream inputStream = planilha.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            return planilhaVendasExtractor.extrair(workbook);
        }
    }

    /**
     * Fluxo completo de importação: extrai os itens vendidos de qualquer layout
     * de planilha e aplica a baixa de estoque (UPDATE em {@code insumo} +
     * INSERT em {@code saida_estoque}) em uma única transação.
     *
     * @return os itens extraídos da planilha, em formato JSON
     */
    @Transactional
    public List<ItemVendido> importarPlanilha(MultipartFile planilha) throws IOException {
        List<ItemVendido> itens = lerPlanilha(planilha);
        List<ItemVendido> agregados = ItemVendido.agregarPorNome(itens);
        baixarEstoque(agregados);
        return agregados;
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
}