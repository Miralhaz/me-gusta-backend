package school.sptech.megusta.mapper;

import school.sptech.megusta.dto.categoria_insumo.CategoriaInsumoRequestDto;
import school.sptech.megusta.dto.categoria_insumo.CategoriaInsumoResponseDto;
import school.sptech.megusta.model.CategoriaInsumo;

import java.util.List;

public class CategoriaInsumoMapper {

    public static CategoriaInsumo toEntity(CategoriaInsumoRequestDto categoriaInsumoRequestDto){
        CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
        categoriaInsumo.setNome(categoriaInsumoRequestDto.getNome());
        return categoriaInsumo;
    }

    public static CategoriaInsumoResponseDto toResponseDto(CategoriaInsumo categoriaInsumo){
        CategoriaInsumoResponseDto categoriaInsumoResponseDto = new CategoriaInsumoResponseDto();
        categoriaInsumoResponseDto.setId(categoriaInsumo.getId());
        categoriaInsumoResponseDto.setNome(categoriaInsumo.getNome());
        return categoriaInsumoResponseDto;
    }

    public static List<CategoriaInsumoResponseDto> toResponseDtoList(List<CategoriaInsumo> categoriaInsumos){
        return categoriaInsumos.stream().map(CategoriaInsumoMapper::toResponseDto).toList();
    }

}
