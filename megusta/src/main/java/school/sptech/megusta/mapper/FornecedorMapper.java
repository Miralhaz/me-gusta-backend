package school.sptech.megusta.mapper;

import school.sptech.megusta.dto.fornecedor.FornecedorRequest;
import school.sptech.megusta.dto.fornecedor.FornecedorResponse;
import school.sptech.megusta.model.Fornecedor;

import java.util.List;

public class FornecedorMapper {

    public static Fornecedor toEntity(FornecedorRequest request){
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome(request.getNome());
        fornecedor.setCnpj(request.getCnpj());
        fornecedor.setTelefone(request.getTelefone());
        return fornecedor;
    }

    public static FornecedorResponse toResponse(Fornecedor fornecedor){
        FornecedorResponse response = new FornecedorResponse();
        response.setId(fornecedor.getId());
        response.setNome(fornecedor.getNome());
        response.setCnpj(fornecedor.getCnpj());
        response.setTelefone(fornecedor.getTelefone());
        response.setAtivo(fornecedor.getAtivo());
        return response;
    }

    public static List<FornecedorResponse> toResponseList(List<Fornecedor> fornecedores){
        return fornecedores.stream()
                .map(FornecedorMapper::toResponse)
                .toList();
    }
}
