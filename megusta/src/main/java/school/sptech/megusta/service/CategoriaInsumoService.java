package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.dto.categoria_insumo.CategoriaInsumoRequestDto;
import school.sptech.megusta.dto.categoria_insumo.CategoriaInsumoResponseDto;
import school.sptech.megusta.exception.CategoriaInsumoConflitoException;
import school.sptech.megusta.exception.CategoriaInsumoNaoEncontradaException;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.repository.CategoriaInsumoRepository;

import java.util.List;
import java.util.Optional;

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
                .orElseThrow(() -> new CategoriaInsumoNaoEncontradaException("Categoria não encontrada."));
        return categoriaInsumo;
    }

    public CategoriaInsumo cadastrar(CategoriaInsumo categoriaACadastrar){
        boolean existe = categoriaInsumoRepository.existsByNome(categoriaACadastrar.getNome());
        if (existe){
            throw new CategoriaInsumoConflitoException("Categoria de insumo já existe!");
        }
        return categoriaInsumoRepository.save(categoriaACadastrar);
    }

    public void deletar(Integer id){
        boolean existe = categoriaInsumoRepository.existsById(id);
        if (!existe){
            throw new CategoriaInsumoNaoEncontradaException("Categoria de insumo não encontrada.");
        }
        categoriaInsumoRepository.deleteById(id);
    }
}
