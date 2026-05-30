package school.sptech.megusta.mapper;

import school.sptech.megusta.dto.saida_estoque.SaidaEstoqueRequest;
import school.sptech.megusta.dto.saida_estoque.SaidaEstoqueResponse;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.Motivo;
import school.sptech.megusta.model.SaidaEstoque;
import school.sptech.megusta.model.Usuario;

import java.util.List;

public class SaidaEstoqueMapper {

    public static SaidaEstoque toEntity(SaidaEstoqueRequest request) {
        SaidaEstoque entity = new SaidaEstoque();

        Insumo insumo = new Insumo();
        insumo.setId(request.getFkInsumo());
        entity.setInsumo(insumo);

        Usuario usuario = new Usuario();
        usuario.setId(request.getFkUsuario());
        entity.setUsuario(usuario);

        Motivo motivo = new Motivo();
        motivo.setId(request.getFkMotivo());
        entity.setMotivo(motivo);

        entity.setQuantidade(request.getQuantidade());

        return entity;
    }

    public static SaidaEstoqueResponse toResponse(SaidaEstoque entity) {
        SaidaEstoqueResponse resp = new SaidaEstoqueResponse();
        resp.setId(entity.getId());
        resp.setQuantidade(entity.getQuantidade());
        resp.setDtSaida(entity.getDtSaida());

        SaidaEstoqueResponse.InsumoSaida insumo = new SaidaEstoqueResponse.InsumoSaida();
        if (entity.getInsumo() != null) {
            insumo.setId(entity.getInsumo().getId());
            insumo.setNome(entity.getInsumo().getNome());
        }
        resp.setInsumo(insumo);

        SaidaEstoqueResponse.UsuarioSaida usuario = new SaidaEstoqueResponse.UsuarioSaida();
        if (entity.getUsuario() != null) {
            usuario.setId(entity.getUsuario().getId());
            usuario.setNome(entity.getUsuario().getNome());
        }
        resp.setUsuario(usuario);

        SaidaEstoqueResponse.MotivoSaida motivo = new SaidaEstoqueResponse.MotivoSaida();
        if (entity.getMotivo() != null) {
            motivo.setId(entity.getMotivo().getId());
            motivo.setNome(entity.getMotivo().getNome());
        }
        resp.setMotivo(motivo);

        return resp;
    }

    public static List<SaidaEstoqueResponse> toResponse(List<SaidaEstoque> list) {
        return list.stream().map(SaidaEstoqueMapper::toResponse).toList();
    }
}