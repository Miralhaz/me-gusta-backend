package school.sptech.megusta.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.megusta.dto.categoria_fogazza.CategoriaFogazzaRequestDto;
import school.sptech.megusta.dto.categoria_fogazza.CategoriaFogazzaResponseDto;
import school.sptech.megusta.dto.categoria_insumo.CategoriaInsumoRequestDto;
import school.sptech.megusta.dto.categoria_insumo.CategoriaInsumoResponseDto;
import school.sptech.megusta.mapper.CategoriaFogazzaMapper;
import school.sptech.megusta.mapper.CategoriaInsumoMapper;
import school.sptech.megusta.model.CategoriaFogazza;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.service.CategoriaFogazzaService;

import java.util.List;

@RestController
@RequestMapping("/categoria-fogazza")
public class CategoriaFogazzaController {

    private final CategoriaFogazzaService service;

    public CategoriaFogazzaController(CategoriaFogazzaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaFogazzaResponseDto>> listar(){
        List<CategoriaFogazza> categoriaFogazzaList = service.listar();
        if (categoriaFogazzaList.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CategoriaFogazzaMapper.toResponseDtoList(categoriaFogazzaList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaFogazzaResponseDto> buscarPorId(@PathVariable Integer id){
        CategoriaFogazza categoriaFogazzaCapturada = service.buscarPorId(id);
        return ResponseEntity.ok(CategoriaFogazzaMapper.toResponseDto(categoriaFogazzaCapturada));
    }

    @PostMapping
    public ResponseEntity<CategoriaFogazzaResponseDto> cadastrar(
            @RequestBody @Valid CategoriaFogazzaRequestDto request
    ){
        CategoriaFogazza categoriaFogazza = CategoriaFogazzaMapper.toEntity(request);
        CategoriaFogazza categoriaCadastrada = service.cadastrar(categoriaFogazza);
        return ResponseEntity.status(201).body(CategoriaFogazzaMapper.toResponseDto
                (categoriaCadastrada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

