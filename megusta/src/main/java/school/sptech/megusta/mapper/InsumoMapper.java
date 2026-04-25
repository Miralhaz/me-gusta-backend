package school.sptech.megusta.mapper;

import school.sptech.megusta.dto.insumo.InsumoRequest;
import school.sptech.megusta.dto.insumo.InsumoResponse;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.TipoStatus;
import school.sptech.megusta.model.UnidadeMedida;

import java.util.List;

public class InsumoMapper {

    public static InsumoResponse toResponse(Insumo insumo){

        InsumoResponse.InsumoCategoria insumoCategoria = new InsumoResponse.InsumoCategoria();
        InsumoResponse.UnidadeInsumo unidadeInsumo = new InsumoResponse.UnidadeInsumo();
        InsumoResponse.TipoStatusInsumo tipoStatusInsumo = new InsumoResponse.TipoStatusInsumo();

        insumoCategoria.setId(insumo.getCategoriaInsumo().getId());
        insumoCategoria.setNome(insumo.getCategoriaInsumo().getNome());

        unidadeInsumo.setId(insumo.getUnidadeMedida().getId());
        unidadeInsumo.setUnidade(insumo.getUnidadeMedida().getUnidade());

        tipoStatusInsumo.setId(insumo.getTipoStatus().getId());
        tipoStatusInsumo.setNome(insumo.getTipoStatus().getNome());

        InsumoResponse response = new InsumoResponse();

        response.setId(insumo.getId());
        response.setNome(insumo.getNome());
        response.setCodigoInsumo(insumo.getCodigoInsumo());
        response.setEstoqueMinimo(insumo.getEstoqueMinimo());
        response.setQuantidadeAtual(insumo.getQuantidadeAtual());
        response.setAtivo(insumo.isAtivo());
        response.setInsumoCategoria(insumoCategoria);
        response.setUnidadeInsumo(unidadeInsumo);
        response.setTipoStatus(tipoStatusInsumo);

        return response;

    }

    public static Insumo toEntity(InsumoRequest request){
        Insumo insumo = new Insumo();

        insumo.setNome(request.getNome());
        insumo.setCodigoInsumo(request.getCodigoInsumo());
        insumo.setEstoqueMinimo(request.getEstoqueMinimo());
        insumo.setQuantidadeAtual(request.getQuantidadeAtual());
        insumo.setAtivo(request.isAtivo());

        CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
        categoriaInsumo.setId(request.getFkCategoriaInsumo());
        insumo.setCategoriaInsumo(categoriaInsumo);

        UnidadeMedida unidadeMedida = new UnidadeMedida();
        unidadeMedida.setId(request.getFkUnidadeMedida());
        insumo.setUnidadeMedida(unidadeMedida);

        TipoStatus tipoStatus = new TipoStatus();
        tipoStatus.setId(request.getFkStatus());
        insumo.setTipoStatus(tipoStatus);

        return insumo;
    }

    public static List<InsumoResponse> toResponse(List<Insumo> insumos){
        return insumos.stream()
                .map(InsumoMapper::toResponse)
                .toList();
    }

}
