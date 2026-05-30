package school.sptech.megusta.mapper;

import school.sptech.megusta.dto.motivo.MotivoRequest;
import school.sptech.megusta.dto.motivo.MotivoResponse;
import school.sptech.megusta.model.Motivo;

import java.util.List;

public class MotivoMapper {

    public static Motivo toEntity(MotivoRequest request){
        Motivo motivo = new Motivo();

        motivo.setNome(request.getNome());

        return motivo;
    }

    public static MotivoResponse toResponse(Motivo entity){
        MotivoResponse motivoResponse = new MotivoResponse();

        motivoResponse.setId(entity.getId());
        motivoResponse.setNome(entity.getNome());

        return motivoResponse;
    }

    public static List<MotivoResponse> toResponse(List<Motivo> entityList){
        return entityList.stream().map(MotivoMapper::toResponse).toList();
    }
}
