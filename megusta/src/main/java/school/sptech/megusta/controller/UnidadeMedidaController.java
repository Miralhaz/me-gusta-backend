package school.sptech.megusta.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.megusta.dto.unidade_medida.UnidadeMedidaRequest;
import school.sptech.megusta.dto.unidade_medida.UnidadeMedidaResponse;
import school.sptech.megusta.mapper.UnidadeMedidaMapper;
import school.sptech.megusta.model.UnidadeMedida;
import school.sptech.megusta.service.UnidadeMedidaService;

import java.util.List;

@RestController
@RequestMapping("/unidade-medidas")
public class UnidadeMedidaController {

    private final UnidadeMedidaService unidadeMedidaService;

    public UnidadeMedidaController(UnidadeMedidaService unidadeMedidaService) {
        this.unidadeMedidaService = unidadeMedidaService;
    }

    @GetMapping
    public ResponseEntity<List<UnidadeMedidaResponse>> listar(){
        List<UnidadeMedida> unidadeMedidas = unidadeMedidaService.listar();
        if (unidadeMedidas.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(UnidadeMedidaMapper.toResponse(unidadeMedidas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadeMedidaResponse> buscarPorId(@PathVariable Integer id){
        UnidadeMedida unidadeMedida = unidadeMedidaService.buscarPorId(id);
        return ResponseEntity.ok(UnidadeMedidaMapper.toResponse(unidadeMedida));
    }

    @PostMapping
    public ResponseEntity<UnidadeMedidaResponse> cadastrar(@RequestBody @Valid UnidadeMedidaRequest request){
        UnidadeMedida unidadeMedida = UnidadeMedidaMapper.toEntity(request);
        UnidadeMedida salvo = unidadeMedidaService.cadastrar(unidadeMedida);
        return ResponseEntity.status(201).body(UnidadeMedidaMapper.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadeMedidaResponse> atualizar(
            @RequestBody @Valid UnidadeMedidaRequest request,
            @PathVariable Integer id
    ){
        UnidadeMedida unidadeMedida = UnidadeMedidaMapper.toEntity(request);
        UnidadeMedida atualizada = unidadeMedidaService.atualizar(unidadeMedida, id);
        return ResponseEntity.ok(UnidadeMedidaMapper.toResponse(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id){
        unidadeMedidaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
