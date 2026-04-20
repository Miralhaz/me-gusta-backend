package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.CategoriaFogazzaNaoEncontradaException;
import school.sptech.megusta.exception.FogazzaConflitoException;
import school.sptech.megusta.exception.FogazzaNaoEncontradaException;
import school.sptech.megusta.model.CategoriaFogazza;
import school.sptech.megusta.model.Fogazzas;
import school.sptech.megusta.repository.CategoriaFogazzaRepository;
import school.sptech.megusta.repository.FogazzasRepository;

import java.util.List;

@Service
public class FogazzasService {
    private final FogazzasRepository fogazzasRepository;
    private final CategoriaFogazzaRepository categoriaFogazzaRepository;

    public FogazzasService(FogazzasRepository fogazzasRepository, CategoriaFogazzaRepository categoriaFogazzaRepository) {
        this.fogazzasRepository = fogazzasRepository;
        this.categoriaFogazzaRepository = categoriaFogazzaRepository;
    }

    public List<Fogazzas> listar() {
        return fogazzasRepository.findAll();
    }

    public Fogazzas buscarPorId(Integer id) {
        return fogazzasRepository.findById(id)
                .orElseThrow(() -> new FogazzaNaoEncontradaException("Fogazza não encontrada."));
    }

    public Fogazzas cadastrar(Fogazzas fogazza, Integer categoriaFogazzaId) {
        boolean existe = fogazzasRepository.existsByNome(fogazza.getNome());
        if (existe) {
            throw new FogazzaConflitoException("Já existe uma Fogazza com esse nome!");
        }

        CategoriaFogazza categoria = categoriaFogazzaRepository.findById(categoriaFogazzaId)
                .orElseThrow(() -> new CategoriaFogazzaNaoEncontradaException("Sabor não encontrado."));

        fogazza.setCategoriaFogazza(categoria);
        return fogazzasRepository.save(fogazza);
    }

    public Fogazzas atualizar(Integer id, Fogazzas fogazzaAtualizada, Integer categoriaFogazzaId) {
        fogazzasRepository.findById(id)
                .orElseThrow(() -> new FogazzaNaoEncontradaException("Fogazza não encontrada."));

        CategoriaFogazza categoria = categoriaFogazzaRepository.findById(categoriaFogazzaId)
                .orElseThrow(() -> new CategoriaFogazzaNaoEncontradaException("Sabor não encontrado."));

        fogazzaAtualizada.setId(id);
        fogazzaAtualizada.setCategoriaFogazza(categoria);
        return fogazzasRepository.save(fogazzaAtualizada);
    }

    public void deletar(Integer id) {
        if (!fogazzasRepository.existsById(id)) {
            throw new FogazzaNaoEncontradaException("Fogazza não encontrada.");
        }
        fogazzasRepository.deleteById(id);
    }
}
