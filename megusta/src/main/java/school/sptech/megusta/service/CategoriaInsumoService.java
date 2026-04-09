package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.dto.usuario.CategoriaInsumoRequestDto;
import school.sptech.megusta.dto.usuario.CategoriaInsumoResponseDto;
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
        Optional<CategoriaInsumo> categoriaOptional = categoriaInsumoRepository.findById(id);
        if (categoriaOptional.isEmpty()){
            throw new CategoriaInsumoNaoEncontradaException(id);
        }
        return categoriaOptional.get();
    }

    public CategoriaInsumo cadastrar(CategoriaInsumo categoriaACadastrar){
        boolean existe = categoriaInsumoRepository.existsByNome(categoriaACadastrar.getNome());
        if (existe){
            throw new CategoriaInsumoConflitoException("Categoria de insumo já existe!");
        }
        categoriaInsumoRepository.save(categoriaACadastrar);
        return categoriaACadastrar;
    }

    public void deletar(Integer id){
        boolean existe = categoriaInsumoRepository.existsById(id);
        if (!existe){
            throw new CategoriaInsumoNaoEncontradaException(id);
        }
        categoriaInsumoRepository.deleteById(id);
    }
}
