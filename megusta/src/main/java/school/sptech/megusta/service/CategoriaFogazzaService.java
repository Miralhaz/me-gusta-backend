package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.CategoriaFogazza;
import school.sptech.megusta.repository.CategoriaFogazzaRepository;

import java.util.List;

@Service
public class CategoriaFogazzaService {

    private final CategoriaFogazzaRepository categoriaFogazzaRepository;

    public CategoriaFogazzaService(CategoriaFogazzaRepository categoriaFogazzaRepository) {
        this.categoriaFogazzaRepository = categoriaFogazzaRepository;
    }

    public List<CategoriaFogazza> listar() {
        return categoriaFogazzaRepository.findAll();
    }

    public CategoriaFogazza buscarPorId(Integer id) {
        CategoriaFogazza categoriaFogazza = categoriaFogazzaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sabor não encontrado."));
        return categoriaFogazza;
    }

    public CategoriaFogazza cadastrar(CategoriaFogazza categoriaParaCadastrar) {
        boolean existe = categoriaFogazzaRepository.existsByNome(categoriaParaCadastrar.getNome());
        if (existe) {
            throw new RecursoConflitoException("Sabor de Fogazza já existe!");
        }
        return categoriaFogazzaRepository.save(categoriaParaCadastrar);
    }

    public void deletar(Integer id){
        boolean existe = categoriaFogazzaRepository.existsById(id);
        if (!existe){
            throw new RecursoNaoEncontradoException("Sabor de Fogazza não encontrada.");
        }
        categoriaFogazzaRepository.deleteById(id);
    }

}

