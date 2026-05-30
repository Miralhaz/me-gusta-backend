package school.sptech.megusta.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.sptech.megusta.dto.motivo.MotivoRequest;
import school.sptech.megusta.dto.motivo.MotivoResponse;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.mapper.MotivoMapper;
import school.sptech.megusta.model.Motivo;
import school.sptech.megusta.repository.MotivoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MotivoService {

    private final MotivoRepository motivoRepository;

    public List<MotivoResponse> buscarTodos(){
        return MotivoMapper.toResponse(motivoRepository.findAll());
    }

    public MotivoResponse buscarPorId(Integer id){
        Optional<Motivo> motivoOptional = motivoRepository.findById(id);

        if (motivoOptional.isEmpty()){
            throw new RecursoNaoEncontradoException("Motivo não encontrado!");
        }

        return MotivoMapper.toResponse(motivoOptional.get());
    }

    public MotivoResponse registrar(MotivoRequest request){
        if (motivoRepository.existsByNomeIgnoreCase(request.getNome())){
            throw new RecursoConflitoException("Motivo com o mesmo nome já existe!");
        }

        Motivo motivo = MotivoMapper.toEntity(request);

        return MotivoMapper.toResponse(motivoRepository.save(motivo));
    }

    public MotivoResponse atualizar(Integer id, MotivoRequest request){
        if (motivoRepository.existsByNomeIgnoreCase(request.getNome())){
            throw new RecursoConflitoException("Motivo com o mesmo nome já existe!");
        }

        if (!motivoRepository.existsById(id)){
            throw new RecursoNaoEncontradoException("Motivo não encontrado!");
        }

        Motivo motivo = MotivoMapper.toEntity(request);
        motivo.setId(id);

        return MotivoMapper.toResponse(motivoRepository.save(motivo));
    }

    public void deletarPorId(Integer id){
        if (!motivoRepository.existsById(id)){
            throw new RecursoNaoEncontradoException("Motivo não encontrado!");
        }

        motivoRepository.deleteById(id);
    }
}
