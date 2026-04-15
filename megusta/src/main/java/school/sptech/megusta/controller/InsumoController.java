package school.sptech.megusta.controller;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.megusta.dto.insumo.InsumoRequest;
import school.sptech.megusta.dto.insumo.InsumoResponse;
import school.sptech.megusta.mapper.InsumoMapper;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.service.InsumoService;

import java.util.List;

@RestController
@RequestMapping("/insumos")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @GetMapping
    public ResponseEntity<List<InsumoResponse>> listar(){
        List<Insumo> insumos = insumoService.listar();
        return ResponseEntity.ok(InsumoMapper.toResponse(insumos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoResponse> buscarPorId(@PathVariable Integer id){
        Insumo insumo = insumoService.buscarPorId(id);
        return ResponseEntity.ok(InsumoMapper.toResponse(insumo));
    }

    @PostMapping
    public ResponseEntity<InsumoResponse> cadastrar(@RequestBody @Valid InsumoRequest request){
        Insumo insumo = InsumoMapper.toEntity(request);
        Insumo insumoCriado = insumoService.cadastrar(insumo);
        return ResponseEntity.status(201).body(InsumoMapper.toResponse(insumoCriado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsumoResponse> atualizar(
            @RequestBody @Valid InsumoRequest request,
            @PathVariable Integer id
    ){
        Insumo insumo = InsumoMapper.toEntity(request);
        Insumo insumoAtt = insumoService.atualizar(insumo, id);
        return ResponseEntity.ok(InsumoMapper.toResponse(insumoAtt));
    }
}
