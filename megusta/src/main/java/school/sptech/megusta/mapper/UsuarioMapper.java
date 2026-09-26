package school.sptech.megusta.mapper;

import school.sptech.megusta.dto.usuario.UsuarioRequestDto;
import school.sptech.megusta.dto.usuario.UsuarioResponseDto;
import school.sptech.megusta.dto.usuario.UsuarioUpdateDto;
import school.sptech.megusta.model.Usuario;

import java.util.List;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRequestDto dto){
        Usuario user = new Usuario();
        user.setNome(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setSenha(dto.getSenha());
        user.setTelefone(dto.getTelefone());
        return user;
    }

    // Aplica os dados de atualização sobre a entidade já carregada.
    // Não copia a senha — ela não existe no DTO de atualização, e é isso
    // que preserva o hash já gravado.
    public static Usuario toEntity(UsuarioUpdateDto dto, Usuario existente){
        existente.setNome(dto.getNome());
        existente.setEmail(dto.getEmail());
        existente.setTelefone(dto.getTelefone());
        return existente;
    }

    public static UsuarioResponseDto toResponseDto(Usuario user){
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setId(user.getId());
        dto.setNome(user.getNome());
        dto.setEmail(user.getEmail());
        dto.setTelefone(user.getTelefone());
        return dto;
    }

    public static List<UsuarioResponseDto> toResponseDtoList(List<Usuario> usuarios){
        return usuarios.stream()
                .map(UsuarioMapper::toResponseDto)
                .toList();
    }
}
