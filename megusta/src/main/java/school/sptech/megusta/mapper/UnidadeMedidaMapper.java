package school.sptech.megusta.mapper;

import school.sptech.megusta.dto.unidade_medida.UnidadeMedidaRequest;
import school.sptech.megusta.dto.unidade_medida.UnidadeMedidaResponse;
import school.sptech.megusta.model.UnidadeMedida;

import java.util.List;

public class UnidadeMedidaMapper {

    public static UnidadeMedidaResponse toResponse(UnidadeMedida unidadeMedida){
        UnidadeMedidaResponse response = new UnidadeMedidaResponse();
        response.setId(unidadeMedida.getId());
        response.setUnidade(unidadeMedida.getUnidade());
        return response;
    }

    public static UnidadeMedida toEntity(UnidadeMedidaRequest request){
        UnidadeMedida unidadeMedida = new UnidadeMedida();
        unidadeMedida.setUnidade(request.getUnidade());
        return unidadeMedida;
    }

    public static List<UnidadeMedidaResponse> toResponse(List<UnidadeMedida> unidadeMedidas){
        return unidadeMedidas.stream()
                .map(UnidadeMedidaMapper::toResponse)
                .toList();
    }

}
