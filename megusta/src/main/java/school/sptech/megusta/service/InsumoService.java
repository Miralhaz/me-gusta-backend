package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.dto.insumo.InsumoResponseTelaInsumos;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.mapper.InsumoMapper;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.model.EntradaEstoque;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.UnidadeMedida;
import school.sptech.megusta.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InsumoService {

    private final InsumoRepository insumoRepository;
    private final CategoriaInsumoRepository categoriaInsumoRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final TipoStatusService tipoStatusService;
    private final EntradaEstoqueRepository entradaEstoqueRepository;
    private final SaidaEstoqueRepository saidaEstoqueRepository;

    public InsumoService(InsumoRepository insumoRepository, CategoriaInsumoRepository categoriaInsumoRepository, UnidadeMedidaRepository unidadeMedidaRepository, TipoStatusService tipoStatusService, EntradaEstoqueRepository entradaEstoqueRepository, SaidaEstoqueRepository saidaEstoqueRepository) {
        this.insumoRepository = insumoRepository;
        this.categoriaInsumoRepository = categoriaInsumoRepository;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
        this.tipoStatusService = tipoStatusService;
        this.entradaEstoqueRepository = entradaEstoqueRepository;
        this.saidaEstoqueRepository = saidaEstoqueRepository;
    }

    public List<Insumo> listar(){
        return insumoRepository.findAll();
    }

    public List<InsumoResponseTelaInsumos> listarParaTelaEstoqueComGiro() {
        List<Insumo> insumos = insumoRepository.findAll();
        List<InsumoResponseTelaInsumos> dtos = InsumoMapper.toResponseTelaInsumos(insumos);

        for (int i = 0; i < insumos.size(); i++) {
            Insumo entidade = insumos.get(i);
            InsumoResponseTelaInsumos dto = dtos.get(i);

            Double giro = this.calcularGiroMensal(entidade);
            dto.setGiroMensal(giro);
        }

        return dtos;
    }

    public Insumo buscarPorId(Integer id){
        return insumoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo não encontrado."));
    }

    public Map<Integer, LocalDate> buscarProximasValidades(){
        List<EntradaEstoque> entradasFuturas = entradaEstoqueRepository
                .findByDtValidadeGreaterThanEqualOrderByDtValidadeAsc(LocalDate.now());

        Map<Integer, LocalDate> proximasPorInsumo = new HashMap<>();
        for (EntradaEstoque entrada : entradasFuturas) {
            Integer insumoId = entrada.getInsumo().getId();
            proximasPorInsumo.putIfAbsent(insumoId, entrada.getDtValidade());
        }
        return proximasPorInsumo;
    }

    public LocalDate buscarProximaValidade(Integer insumoId){
        List<EntradaEstoque> entradas = entradaEstoqueRepository
                .findByInsumoIdAndDtValidadeGreaterThanEqualOrderByDtValidadeAsc(insumoId, LocalDate.now());
        return entradas.isEmpty() ? null : entradas.get(0).getDtValidade();
    }

    public Insumo cadastrar(Insumo insumo){

        boolean existe = insumoRepository.existsByNomeOrCodigoInsumo(insumo.getNome(), insumo.getCodigoInsumo());
        if (existe){
            throw new RecursoConflitoException("Insumo já existe.");
        }

        CategoriaInsumo categoriaInsumo = categoriaInsumoRepository.findById(insumo.getCategoriaInsumo().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria de insumo não encontrada."));

        UnidadeMedida unidadeMedida = unidadeMedidaRepository.findById(insumo.getUnidadeMedida().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade de medida não encontrada."));

        insumo.setCategoriaInsumo(categoriaInsumo);
        insumo.setUnidadeMedida(unidadeMedida);
        insumo.setTipoStatus(tipoStatusService.calcularStatusEstoque(insumo.getQtdAtual(), insumo.getEstoqueMinimo()));
        return insumoRepository.save(insumo);
    }

    public Insumo atualizar(Insumo insumo, Integer id){

        Insumo insumoExistente = insumoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo não encontrado."));

        CategoriaInsumo categoriaInsumo = categoriaInsumoRepository.findById(insumo.getCategoriaInsumo().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria de insumo não encontrada."));

        UnidadeMedida unidadeMedida = unidadeMedidaRepository.findById(insumo.getUnidadeMedida().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade de medida não encontrada."));

        insumoExistente.setNome(insumo.getNome());
        insumoExistente.setCodigoInsumo(insumo.getCodigoInsumo());
        insumoExistente.setEstoqueMinimo(insumo.getEstoqueMinimo());
        insumoExistente.setQtdAtual(insumo.getQtdAtual());
        insumoExistente.setAtivo(insumo.isAtivo());
        insumoExistente.setCategoriaInsumo(categoriaInsumo);
        insumoExistente.setUnidadeMedida(unidadeMedida);
        insumoExistente.setTipoStatus(tipoStatusService.calcularStatusEstoque(insumoExistente.getQtdAtual(), insumoExistente.getEstoqueMinimo()));
        return insumoRepository.save(insumoExistente);
    }

    // Method auxiliar para calcular o giro mensal do insumo
    public Double calcularGiroMensal(Insumo insumo){
        LocalDateTime dtInicio = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime dtFim = YearMonth.now().atEndOfMonth().atTime(23,59,59);

        Double saidas = (saidaEstoqueRepository.sumQuantidadeSaidasByInsumoAndDateBetween(insumo.getId(), dtInicio, dtFim) != null)
                ? saidaEstoqueRepository.sumQuantidadeSaidasByInsumoAndDateBetween(insumo.getId(), dtInicio, dtFim).doubleValue() : 0.0;

        BigDecimal entradasSum = entradaEstoqueRepository
                .sumQuantidadeTotalEntradaByInsumoAndDateBetween(insumo.getId(), dtInicio, dtFim);
        Double entradas = (entradasSum != null) ? entradasSum.doubleValue() : 0.0;

        Double qtdAtual = insumo.getQtdAtual();
        Double estoqueInicial = qtdAtual - entradas + saidas;
        Double estoqueMedio = (estoqueInicial + qtdAtual) / 2.0;

        return (estoqueMedio > 0) ? saidas / estoqueMedio : 0.0;
    }
}