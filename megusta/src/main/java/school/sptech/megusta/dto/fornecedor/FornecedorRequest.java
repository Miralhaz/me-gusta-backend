package school.sptech.megusta.dto.fornecedor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CNPJ;

public class FornecedorRequest {

    @NotBlank
    @Schema(example = "Laticínios São Paulo")
    private String nome;

    @NotBlank
    @CNPJ
    @Schema(example = "11.222.333/0001-44")
    private String cnpj;

    @NotBlank
    @Schema(example = "(11) 91234-5678")
    private String telefone;

    public FornecedorRequest() {
    }

    public FornecedorRequest(String nome, String cnpj, String telefone) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.telefone = telefone;
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
}
