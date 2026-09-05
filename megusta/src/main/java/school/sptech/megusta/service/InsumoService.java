package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.model.EntradaEstoque;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.UnidadeMedida;
import school.sptech.megusta.repository.CategoriaInsumoRepository;
import school.sptech.megusta.repository.EntradaEstoqueRepository;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.UnidadeMedidaRepository;

import java.time.LocalDate;
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

    public InsumoService(InsumoRepository insumoRepository, CategoriaInsumoRepository categoriaInsumoRepository, UnidadeMedidaRepository unidadeMedidaRepository, TipoStatusService tipoStatusService, EntradaEstoqueRepository entradaEstoqueRepository) {
        this.insumoRepository = insumoRepository;
        this.categoriaInsumoRepository = categoriaInsumoRepository;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
        this.tipoStatusService = tipoStatusService;
        this.entradaEstoqueRepository = entradaEstoqueRepository;
    }

    public List<Insumo> listar(){
        return insumoRepository.findAll();
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
}