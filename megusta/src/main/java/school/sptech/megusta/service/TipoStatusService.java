package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.TipoStatus;
import school.sptech.megusta.repository.TipoStatusRepository;

import java.util.List;

@Service
public class TipoStatusService {

    private static final int ID_STATUS_OK = 1;
    private static final int ID_STATUS_ATENCAO = 2;
    private static final int ID_STATUS_CRITICO = 3;
    private static final double FATOR_ATENCAO = 1.2;

    private final TipoStatusRepository tipoStatusRepository;


    public TipoStatusService(TipoStatusRepository tipoStatusRepository) {
        this.tipoStatusRepository = tipoStatusRepository;
    }

    /**
     * Lógica centralizada para o status de estoque de um insumo.
     * CRÍTICO: quantidade atual já está no mínimo ou abaixo dele.
     * ATENÇÃO: quantidade atual está perto do mínimo (até 20% acima).
     * OK: quantidade confortavelmente acima do mínimo.
     */
    public TipoStatus calcularStatusEstoque(Double quantidadeAtual, Double estoqueMinimo) {
        if (quantidadeAtual == null || estoqueMinimo == null || estoqueMinimo <= 0) {
            return buscarPorId(ID_STATUS_OK);
        }
        if (quantidadeAtual <= estoqueMinimo) {
            return buscarPorId(ID_STATUS_CRITICO);
        }
        if (quantidadeAtual <= estoqueMinimo * FATOR_ATENCAO) {
            return buscarPorId(ID_STATUS_ATENCAO);
        }
        return buscarPorId(ID_STATUS_OK);
    }

    public List<TipoStatus> listar(){
        return tipoStatusRepository.findAll();
    }

    public TipoStatus buscarPorId(Integer id){
        return tipoStatusRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("status não encontrado"));
    }

    public TipoStatus cadastrar(TipoStatus status){
        
        if(tipoStatusRepository.existsByNomeIgnoreCase(status.getNome())){
            throw new RecursoConflitoException("status conflitante");
        }

        return tipoStatusRepository.save(status);
    }

    public TipoStatus atualizar(Integer id, TipoStatus statusParaAtualizar){
        tipoStatusRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("status não encontrado"));

        if(tipoStatusRepository.existsByNomeAndIdNot(statusParaAtualizar.getNome(), id)){
            throw new RecursoConflitoException("status conflitante");
        }
        statusParaAtualizar.setId(id);
        return tipoStatusRepository.save(statusParaAtualizar);
    }

    public void excluir(Integer id){
        tipoStatusRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("status não encontrado"));
        tipoStatusRepository.deleteById(id);
    }

}
