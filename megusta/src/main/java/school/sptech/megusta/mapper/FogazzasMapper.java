package school.sptech.megusta.mapper;

import school.sptech.megusta.dto.Fogazzas.FogazzasRequestDto;
import school.sptech.megusta.dto.Fogazzas.FogazzasResponseDto;
import school.sptech.megusta.model.Fogazzas;

import java.util.List;

public class FogazzasMapper {
    public static Fogazzas toEntity(FogazzasRequestDto dto) {
        Fogazzas fogazza = new Fogazzas();
        fogazza.setNome(dto.getNome());
        fogazza.setPreco(dto.getPreco());
        return fogazza;
    }

    public static FogazzasResponseDto toResponseDto(Fogazzas fogazza) {
        FogazzasResponseDto.CategoriaFogazzaDto categoriaDto =
                new FogazzasResponseDto.CategoriaFogazzaDto();
        categoriaDto.setId(fogazza.getCategoriaFogazza().getId());
        categoriaDto.setNome(fogazza.getCategoriaFogazza().getNome());

        FogazzasResponseDto fogazzasResponseDto = new FogazzasResponseDto();
        fogazzasResponseDto.setId(fogazza.getId());
        fogazzasResponseDto.setNome(fogazza.getNome());
        fogazzasResponseDto.setPreco(fogazza.getPreco());
        fogazzasResponseDto.setCategoriaFogazza(categoriaDto);

        return fogazzasResponseDto;
    }

    public static List<FogazzasResponseDto> toResponseDtoList(List<Fogazzas> fogazzaList) {
        return fogazzaList.stream().map(FogazzasMapper::toResponseDto).toList();
    }
}
