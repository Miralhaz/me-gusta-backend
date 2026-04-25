package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.repository.CategoriaInsumoRepository;

import java.util.List;

@Service
public class CategoriaInsumoService {

    private final CategoriaInsumoRepository categoriaInsumoRepository;

    public CategoriaInsumoService(CategoriaInsumoRepository categoriaInsumoRepository) {
        this.categoriaInsumoRepository = categoriaInsumoRepository;
    }

    public List<CategoriaInsumo> listar(){
        return categoriaInsumoRepository.findAll();
    }

    public CategoriaInsumo buscarPorId(Integer id){
        CategoriaInsumo categoriaInsumo = categoriaInsumoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));
        return categoriaInsumo;
    }

    public CategoriaInsumo cadastrar(CategoriaInsumo categoriaACadastrar){
        boolean existe = categoriaInsumoRepository.existsByNome(categoriaACadastrar.getNome());
        if (existe){
            throw new RecursoConflitoException("Categoria de insumo já existe!");
        }
        return categoriaInsumoRepository.save(categoriaACadastrar);
    }

    public void deletar(Integer id){
        boolean existe = categoriaInsumoRepository.existsById(id);
        if (!existe){
            throw new RecursoNaoEncontradoException("Categoria de insumo não encontrada.");
        }
        categoriaInsumoRepository.deleteById(id);
    }
}
