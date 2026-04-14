package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.StatusConflitoException;
import school.sptech.megusta.exception.StatusNaoEncontradoException;
import school.sptech.megusta.model.TipoStatus;
import school.sptech.megusta.repository.TipoStatusRepository;

import java.util.List;

@Service
public class TipoStatusService {

    private final TipoStatusRepository tipoStatusRepository;


    public TipoStatusService(TipoStatusRepository tipoStatusRepository) {
        this.tipoStatusRepository = tipoStatusRepository;
    }

    public List<TipoStatus> listar(){
        return tipoStatusRepository.findAll();
    }

    public TipoStatus buscarPorId(Integer id){
        return tipoStatusRepository.findById(id)
                .orElseThrow(() -> new StatusNaoEncontradoException("status não econtrado"));
    }

    public TipoStatus cadastrar(TipoStatus status){
        
        if(tipoStatusRepository.existsByNomeIgnoreCase(status.getNome())){
            throw new StatusConflitoException("status conflitante");
        }

        return tipoStatusRepository.save(status);
    }

    public TipoStatus atualizar(Integer id, TipoStatus statusParaAtualizar){
        tipoStatusRepository.findById(id)
                .orElseThrow(() -> new StatusNaoEncontradoException("status não econtrado"));

        if(tipoStatusRepository.existsByNomeAndIdNot(statusParaAtualizar.getNome(), id)){
            throw new StatusConflitoException("status conflitante");
        }
        statusParaAtualizar.setId(id);
        return tipoStatusRepository.save(statusParaAtualizar);
    }

    public void excluir(Integer id){
        tipoStatusRepository.findById(id)
                .orElseThrow(() -> new StatusNaoEncontradoException("status não encontrado"));
        tipoStatusRepository.deleteById(id);
    }

}
