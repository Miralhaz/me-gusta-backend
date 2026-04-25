package school.sptech.megusta.mapper;

import school.sptech.megusta.dto.categoria_fogazza.CategoriaFogazzaRequestDto;
import school.sptech.megusta.dto.categoria_fogazza.CategoriaFogazzaResponseDto;
import school.sptech.megusta.model.CategoriaFogazza;

import java.util.List;

public class CategoriaFogazzaMapper {

    public static CategoriaFogazza toEntity(CategoriaFogazzaRequestDto categoriaFogazzaRequestDto){
        CategoriaFogazza categoriaFogazza = new CategoriaFogazza();
        categoriaFogazza.setNome(categoriaFogazzaRequestDto.getNome());
        return categoriaFogazza;
    }

    public static CategoriaFogazzaResponseDto toResponseDto(CategoriaFogazza categoriaFogazza){
        CategoriaFogazzaResponseDto categoriaFogazzaResponseDto = new CategoriaFogazzaResponseDto();
        categoriaFogazzaResponseDto.setId(categoriaFogazza.getId());
        categoriaFogazzaResponseDto.setNome(categoriaFogazza.getNome());
        return categoriaFogazzaResponseDto;
    }

    public static List<CategoriaFogazzaResponseDto> toResponseDtoList(List<CategoriaFogazza> categoriaFogazzaList){
        return categoriaFogazzaList.stream().map(CategoriaFogazzaMapper::toResponseDto).toList();
    }

}