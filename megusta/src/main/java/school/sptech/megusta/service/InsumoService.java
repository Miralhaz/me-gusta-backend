package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.CategoriaInsumoNaoEncontradaException;
import school.sptech.megusta.exception.InsumoNaoEncontradoException;
import school.sptech.megusta.exception.UnidadeMedidaNaoEncontradaException;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.UnidadeMedida;
import school.sptech.megusta.repository.CategoriaInsumoRepository;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.UnidadeMedidaRepository;

import java.util.List;

@Service
public class InsumoService {

    private final InsumoRepository insumoRepository;
    private final CategoriaInsumoRepository categoriaInsumoRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;

    public InsumoService(CategoriaInsumoRepository categoriaInsumoRepository, InsumoRepository insumoRepository, UnidadeMedidaRepository unidadeMedidaRepository) {
        this.categoriaInsumoRepository = categoriaInsumoRepository;
        this.insumoRepository = insumoRepository;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
    }

    public List<Insumo> listar(){
        return insumoRepository.findAll();
    }

    public Insumo buscarPorId(Integer id){
        return insumoRepository.findById(id)
                .orElseThrow(() -> new InsumoNaoEncontradoException("Insumo não encontrado."));
    }

    public Insumo cadastrar(Insumo insumo){
        CategoriaInsumo categoriaInsumo = categoriaInsumoRepository.findById(insumo.getCategoriaInsumo().getId())
                .orElseThrow(() -> new CategoriaInsumoNaoEncontradaException("Categoria de insumo não encontrada."));

        UnidadeMedida unidadeMedida = unidadeMedidaRepository.findById(insumo.getUnidadeMedida().getId())
                .orElseThrow(() -> new UnidadeMedidaNaoEncontradaException("Unidade de medida não encontrada."));

        insumo.setCategoriaInsumo(categoriaInsumo);
        insumo.setUnidadeMedida(unidadeMedida);
        return insumoRepository.save(insumo);
    }

    public Insumo atualizar(Insumo insumo, Integer id){

        Insumo insumoExistente = insumoRepository.findById(id)
                .orElseThrow(() -> new InsumoNaoEncontradoException("Insumo não encontrado."));

        CategoriaInsumo categoriaInsumo = categoriaInsumoRepository.findById(insumo.getCategoriaInsumo().getId())
                .orElseThrow(() -> new CategoriaInsumoNaoEncontradaException("Categoria de insumo não encontrada."));

        UnidadeMedida unidadeMedida = unidadeMedidaRepository.findById(insumo.getUnidadeMedida().getId())
                .orElseThrow(() -> new UnidadeMedidaNaoEncontradaException("Unidade de medida não encontrada."));

        insumoExistente.setId(id);
        insumoExistente.setCategoriaInsumo(categoriaInsumo);
        insumoExistente.setUnidadeMedida(unidadeMedida);
        return insumoRepository.save(insumo);
    }
}
