package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.TipoStatus;
import school.sptech.megusta.model.UnidadeMedida;
import school.sptech.megusta.repository.CategoriaInsumoRepository;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.TipoStatusRepository;
import school.sptech.megusta.repository.UnidadeMedidaRepository;

import java.util.List;

@Service
public class InsumoService {

    private final InsumoRepository insumoRepository;
    private final CategoriaInsumoRepository categoriaInsumoRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final TipoStatusRepository tipoStatusRepository;

    public InsumoService(InsumoRepository insumoRepository, CategoriaInsumoRepository categoriaInsumoRepository, UnidadeMedidaRepository unidadeMedidaRepository, TipoStatusRepository tipoStatusRepository) {
        this.insumoRepository = insumoRepository;
        this.categoriaInsumoRepository = categoriaInsumoRepository;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
        this.tipoStatusRepository = tipoStatusRepository;
    }

    public List<Insumo> listar(){
        return insumoRepository.findAll();
    }

    public Insumo buscarPorId(Integer id){
        return insumoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo não encontrado."));
    }

    public Insumo cadastrar(Insumo insumo){
        CategoriaInsumo categoriaInsumo = categoriaInsumoRepository.findById(insumo.getCategoriaInsumo().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria de insumo não encontrada."));

        UnidadeMedida unidadeMedida = unidadeMedidaRepository.findById(insumo.getUnidadeMedida().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade de medida não encontrada."));

        TipoStatus tipoStatus = tipoStatusRepository.findById(insumo.getTipoStatus().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Status não encontrada."));

        insumo.setCategoriaInsumo(categoriaInsumo);
        insumo.setUnidadeMedida(unidadeMedida);
        insumo.setTipoStatus(tipoStatus);
        return insumoRepository.save(insumo);
    }

    public Insumo atualizar(Insumo insumo, Integer id){

        Insumo insumoExistente = insumoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo não encontrado."));

        CategoriaInsumo categoriaInsumo = categoriaInsumoRepository.findById(insumo.getCategoriaInsumo().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria de insumo não encontrada."));

        UnidadeMedida unidadeMedida = unidadeMedidaRepository.findById(insumo.getUnidadeMedida().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade de medida não encontrada."));

        TipoStatus tipoStatus = tipoStatusRepository.findById(insumo.getTipoStatus().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Status não encontrado."));

        insumoExistente.setId(id);
        insumoExistente.setCategoriaInsumo(categoriaInsumo);
        insumoExistente.setUnidadeMedida(unidadeMedida);
        insumoExistente.setTipoStatus(tipoStatus);
        return insumoRepository.save(insumo);
    }
}
