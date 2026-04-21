package school.sptech.megusta.dto.fornecedor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CNPJ;

public class FornecedorResponse {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Laticínios São Paulo")
    private String nome;

    @Schema(example = "11.222.333/0001-44")
    private String cnpj;

    @Schema(example = "(11) 91234-5678")
    private String telefone;

    @Schema(example = "true")
    private Boolean ativo;

    public FornecedorResponse() {
    }

    public FornecedorResponse(Integer id, String nome, String cnpj, String telefone, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.telefone = telefone;
        this.ativo = ativo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
