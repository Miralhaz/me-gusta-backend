package school.sptech.megusta.dto.tipo_status;

public class TipoStatusResponse {

    private Integer id;
    private String nome;

    public TipoStatusResponse() {
    }

    public TipoStatusResponse(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
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
}
