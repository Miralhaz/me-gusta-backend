package school.sptech.megusta.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.megusta.dto.categoria_insumo.CategoriaInsumoRequestDto;
import school.sptech.megusta.dto.categoria_insumo.CategoriaInsumoResponseDto;
import school.sptech.megusta.mapper.CategoriaInsumoMapper;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.service.CategoriaInsumoService;

import java.util.List;

@RestController
@RequestMapping("/categoria-insumos")
public class CategoriaInsumoController {

    private final CategoriaInsumoService service;

    public CategoriaInsumoController(CategoriaInsumoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaInsumoResponseDto>> listar(){
        List<CategoriaInsumo> categoriaInsumos = service.listar();
        if (categoriaInsumos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CategoriaInsumoMapper.toResponseDtoList(categoriaInsumos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaInsumoResponseDto> buscarPorId(@PathVariable Integer id){
        CategoriaInsumo categoriaInsumoAchada = service.buscarPorId(id);
        return ResponseEntity.ok(CategoriaInsumoMapper.toResponseDto(categoriaInsumoAchada));
    }

    @PostMapping
    public ResponseEntity<CategoriaInsumoResponseDto> cadastrar(
            @RequestBody @Valid CategoriaInsumoRequestDto request
            ){
        CategoriaInsumo categoriaInsumo = CategoriaInsumoMapper.toEntity(request);
        CategoriaInsumo categoriaCadastrada = service.cadastrar(categoriaInsumo);
        return ResponseEntity.status(201).body(CategoriaInsumoMapper.toResponseDto(categoriaCadastrada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
