package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.dto.consumo_categoria.ConsumoCategoriaRequestDto;
import school.sptech.megusta.dto.consumo_categoria.ConsumoCategoriaResponseDto;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.repository.CategoriaInsumoRepository;
import school.sptech.megusta.repository.InsumoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoriaInsumoService {

    private final CategoriaInsumoRepository categoriaInsumoRepository;
    private final InsumoRepository insumoRepository;

    public CategoriaInsumoService(CategoriaInsumoRepository categoriaInsumoRepository, InsumoRepository insumoRepository) {
        this.categoriaInsumoRepository = categoriaInsumoRepository;
        this.insumoRepository = insumoRepository;
    }

    public List<CategoriaInsumo> listar(){
        return categoriaInsumoRepository.findAll();
    }

    public CategoriaInsumo buscarPorId(Integer id) {
        return categoriaInsumoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));
    }

    public CategoriaInsumo cadastrar(CategoriaInsumo categoriaACadastrar) {
        String nomeNormalizado = categoriaACadastrar.getNome().trim();

        boolean existe = categoriaInsumoRepository.existsByNomeIgnoreCase(nomeNormalizado);
        if (existe) {
            throw new RecursoConflitoException("Categoria de insumo já existe!");
        }

        categoriaACadastrar.setNome(nomeNormalizado);
        return categoriaInsumoRepository.save(categoriaACadastrar);
    }

    public CategoriaInsumo atualizar(Integer id, CategoriaInsumo categoriaAtualizada) {
        CategoriaInsumo categoriaExistente = categoriaInsumoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));

        boolean nomeEmUso = categoriaInsumoRepository.existsByNomeIgnoreCaseAndIdNot(
                categoriaAtualizada.getNome().trim(), id
        );

        if (nomeEmUso) {
            throw new RecursoConflitoException("Já existe uma categoria com esse nome.");
        }

        categoriaExistente.setNome(categoriaAtualizada.getNome());
        return categoriaInsumoRepository.save(categoriaExistente);
    }

    public void deletar(Integer id) {
        CategoriaInsumo categoria = categoriaInsumoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));

        boolean possuiInsumos = insumoRepository.existsByCategoriaInsumo(categoria);;
        if (possuiInsumos) {
            throw new RecursoConflitoException(
                    "Não é possível excluir. Existem insumos vinculados a essa categoria."
            );
        }

        categoriaInsumoRepository.delete(categoria);
    }

    public List<Insumo> listarInsumosPorCategoria(Integer categoriaId) {
        CategoriaInsumo categoria = categoriaInsumoRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));

        return insumoRepository.findByCategoriaInsumoId(categoriaId);
    }

    public List<ConsumoCategoriaResponseDto> calcularConsumoPorCategoriaNosUltimosDias(ConsumoCategoriaRequestDto request){
        String nomeCategoria = request.getNomeCategoria();
        Integer intervalo = request.getIntervalo();
        LocalDateTime diasAtras = LocalDateTime.now().minusDays(intervalo);

        return categoriaInsumoRepository.consumoPorCategoriaEspecifica(nomeCategoria, diasAtras);
    }
}
