package school.sptech.megusta.dto.usuario;

public class CategoriaInsumoResponseDto {

    private Integer id;
    private String nome;

    public CategoriaInsumoResponseDto(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public CategoriaInsumoResponseDto() {
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
