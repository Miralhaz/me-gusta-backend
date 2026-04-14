package school.sptech.megusta.mapper;

import school.sptech.megusta.dto.usuario.UsuarioRequestDto;
import school.sptech.megusta.dto.usuario.UsuarioResponseDto;
import school.sptech.megusta.model.Usuario;

import java.util.List;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRequestDto dto){
        Usuario user = new Usuario();
        user.setNome(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setSenha(dto.getSenha());
        return user;
    }

    public static UsuarioResponseDto toResponseDto(Usuario user){
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setId(user.getId());
        dto.setNome(user.getNome());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public static List<UsuarioResponseDto> toResponseDtoList(List<Usuario> usuarios){
        return usuarios.stream()
                .map(UsuarioMapper::toResponseDto)
                .toList();
    }
}
