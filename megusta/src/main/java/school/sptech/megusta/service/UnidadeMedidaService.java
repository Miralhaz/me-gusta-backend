package school.sptech.megusta.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.UnidadeMedidaConflito;
import school.sptech.megusta.exception.UnidadeMedidaNaoEncontradaException;
import school.sptech.megusta.model.UnidadeMedida;
import school.sptech.megusta.repository.UnidadeMedidaRepository;

import java.util.List;

@Service
public class UnidadeMedidaService {

    private final UnidadeMedidaRepository unidadeMedidaRepository;

    public UnidadeMedidaService(UnidadeMedidaRepository unidadeMedidaRepository) {
        this.unidadeMedidaRepository = unidadeMedidaRepository;
    }

    public List<UnidadeMedida> listar(){
        return unidadeMedidaRepository.findAll();
    }

    public UnidadeMedida buscarPorId(Integer id){
        UnidadeMedida unidadeMedida = unidadeMedidaRepository.findById(id)
                .orElseThrow(() -> new UnidadeMedidaNaoEncontradaException("Unidade de medida não encontrada."));
        return unidadeMedida;
    }

    public UnidadeMedida cadastrar(UnidadeMedida unidadeMedida){
        boolean existe = unidadeMedidaRepository.existsByUnidade(unidadeMedida.getUnidade());
        if (existe){
            throw new UnidadeMedidaConflito("Unidade de medida já cadastrada.");
        }
        return unidadeMedidaRepository.save(unidadeMedida);
    }

    public UnidadeMedida atualizar(UnidadeMedida unidadeMedida, Integer id){
        UnidadeMedida unidadeAchada = unidadeMedidaRepository.findById(id)
                .orElseThrow(() -> new UnidadeMedidaNaoEncontradaException("Unidade de medida não encontrada."));
        unidadeAchada.setId(id);
        unidadeAchada.setUnidade(unidadeMedida.getUnidade());
        return unidadeMedidaRepository.save(unidadeAchada);
    }

    public void deletar(Integer id){
        UnidadeMedida unidadeMedida = unidadeMedidaRepository.findById(id)
                .orElseThrow(() -> new UnidadeMedidaNaoEncontradaException("Unidade de medida não encontrada."));
        unidadeMedidaRepository.deleteById(id);
    }

}
