package school.sptech.megusta.dto.usuario;

public class UsuarioResponseDto {
    private Integer id;
    private String nome;
    private String email;

    public UsuarioResponseDto() {
    }

    public UsuarioResponseDto(Integer id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
