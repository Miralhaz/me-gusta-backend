package school.sptech.megusta.mapper;

import school.sptech.megusta.dto.tipo_status.TipoStatusRequest;
import school.sptech.megusta.dto.tipo_status.TipoStatusResponse;
import school.sptech.megusta.model.TipoStatus;

import java.util.List;

public class TipoStatusMapper {

    public static TipoStatus toEntity(TipoStatusRequest request){
        TipoStatus tipoStatus = new TipoStatus();
        tipoStatus.setNome(request.getNome());
        return tipoStatus;
    }

    public static TipoStatusResponse toResponse(TipoStatus status){
        TipoStatusResponse response = new TipoStatusResponse();
        response.setId(status.getId());
        response.setNome(status.getNome());
        return response;
    }

    public static List<TipoStatusResponse> toResponseList(List<TipoStatus> status){
        return status.stream()
                .map(TipoStatusMapper::toResponse).toList();
    }

}
