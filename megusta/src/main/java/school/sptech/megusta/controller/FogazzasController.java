package school.sptech.megusta.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.megusta.dto.Fogazzas.FogazzasRequestDto;
import school.sptech.megusta.dto.Fogazzas.FogazzasResponseDto;
import school.sptech.megusta.mapper.FogazzasMapper;
import school.sptech.megusta.model.Fogazzas;
import school.sptech.megusta.service.FogazzasService;

import java.util.List;

@RestController
@RequestMapping("/fogazzas")
public class FogazzasController {
    private final FogazzasService service;

    public FogazzasController(FogazzasService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<FogazzasResponseDto>> listar() {
        List<Fogazzas> fogazzaList = service.listar();
        if (fogazzaList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(FogazzasMapper.toResponseDtoList(fogazzaList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FogazzasResponseDto> buscarPorId(@PathVariable Integer id) {
        Fogazzas fogazza = service.buscarPorId(id);
        return ResponseEntity.ok(FogazzasMapper.toResponseDto(fogazza));
    }

    @PostMapping
    public ResponseEntity<FogazzasResponseDto> cadastrar(@RequestBody @Valid FogazzasRequestDto request) {
        Fogazzas fogazza = FogazzasMapper.toEntity(request);
        Fogazzas fogazzaCadastrada = service.cadastrar(fogazza, request.getCategoriaFogazzaId());
        return ResponseEntity.status(201).body(FogazzasMapper.toResponseDto(fogazzaCadastrada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FogazzasResponseDto> atualizar(@PathVariable Integer id,
                                                        @RequestBody @Valid
                                                        FogazzasRequestDto request) {
        Fogazzas fogazza = FogazzasMapper.toEntity(request);
        Fogazzas fogazzaAtualizada = service.atualizar(
                id, fogazza, request.getCategoriaFogazzaId());
        return ResponseEntity.ok(FogazzasMapper.toResponseDto(fogazzaAtualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
