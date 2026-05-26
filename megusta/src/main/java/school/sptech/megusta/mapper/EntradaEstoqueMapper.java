package school.sptech.megusta.mapper;

import school.sptech.megusta.dto.entrada_estoque.EntradaEstoqueRequest;
import school.sptech.megusta.dto.entrada_estoque.EntradaEstoqueResponse;
import school.sptech.megusta.model.*;

import java.util.List;

public class EntradaEstoqueMapper {

    public static EntradaEstoqueResponse toResponse(EntradaEstoque entradaEstoque) {
        EntradaEstoqueResponse response = new EntradaEstoqueResponse();

        response.setId(entradaEstoque.getId());
        response.setQuantidade(entradaEstoque.getQuantidade());
        response.setDtEntrada(entradaEstoque.getDtEntrada());
        response.setLote(entradaEstoque.getLote());
        response.setDtValidade(entradaEstoque.getDtValidade());
        response.setDtPedido(entradaEstoque.getDtPedido());
        response.setVlTotal(entradaEstoque.getVlTotal());

        EntradaEstoqueResponse.InsumoEntrada insumo = new EntradaEstoqueResponse.InsumoEntrada();
        insumo.setId(entradaEstoque.getInsumo().getId());
        insumo.setNome(entradaEstoque.getInsumo().getNome());
        insumo.setCodigoInsumo(entradaEstoque.getInsumo().getCodigoInsumo());
        response.setInsumo(insumo);

        EntradaEstoqueResponse.UsuarioEntrada usuario = new EntradaEstoqueResponse.UsuarioEntrada();
        usuario.setId(entradaEstoque.getUsuario().getId());
        usuario.setNome(entradaEstoque.getUsuario().getNome());
        usuario.setEmail(entradaEstoque.getUsuario().getEmail());
        response.setUsuario(usuario);

        EntradaEstoqueResponse.FornecedorEntrada fornecedor = new EntradaEstoqueResponse.FornecedorEntrada();
        fornecedor.setId(entradaEstoque.getFornecedor().getId());
        fornecedor.setNome(entradaEstoque.getFornecedor().getNome());
        fornecedor.setCnpj(entradaEstoque.getFornecedor().getCnpj());
        response.setFornecedor(fornecedor);

        EntradaEstoqueResponse.TipoStatusEntrada tipoStatus = new EntradaEstoqueResponse.TipoStatusEntrada();
        tipoStatus.setId(entradaEstoque.getTipoStatus().getId());
        tipoStatus.setNome(entradaEstoque.getTipoStatus().getNome());
        response.setTipoStatus(tipoStatus);

        EntradaEstoqueResponse.UnidadeMedidaEntrada unidadeMedida = new EntradaEstoqueResponse.UnidadeMedidaEntrada();
        unidadeMedida.setId(entradaEstoque.getUnidadeMedida().getId());
        unidadeMedida.setUnidade(entradaEstoque.getUnidadeMedida().getUnidade());
        response.setUnidadeMedida(unidadeMedida);

        return response;
    }

    public static EntradaEstoque toEntity(EntradaEstoqueRequest request) {
        EntradaEstoque entradaEstoque = new EntradaEstoque();

        entradaEstoque.setQuantidade(request.getQuantidade());
        entradaEstoque.setLote(request.getLote());
        entradaEstoque.setDtValidade(request.getDtValidade());
        entradaEstoque.setDtPedido(request.getDtPedido());
        entradaEstoque.setVlTotal(request.getVlTotal());

        Insumo insumo = new Insumo();
        insumo.setId(request.getFkInsumo());
        entradaEstoque.setInsumo(insumo);

        Usuario usuario = new Usuario();
        usuario.setId(request.getFkUsuario());
        entradaEstoque.setUsuario(usuario);

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(request.getFkFornecedor());
        entradaEstoque.setFornecedor(fornecedor);

        TipoStatus tipoStatus = new TipoStatus();
        tipoStatus.setId(request.getFkTipoStatus());
        entradaEstoque.setTipoStatus(tipoStatus);

        UnidadeMedida unidadeMedida = new UnidadeMedida();
        unidadeMedida.setId(request.getFkUnidadeMedida());
        entradaEstoque.setUnidadeMedida(unidadeMedida);

        return entradaEstoque;
    }

    public static List<EntradaEstoqueResponse> toResponse(List<EntradaEstoque> entradas) {
        return entradas.stream()
                .map(EntradaEstoqueMapper::toResponse)
                .toList();
    }

}