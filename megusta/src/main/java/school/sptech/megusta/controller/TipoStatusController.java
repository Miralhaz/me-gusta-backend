package school.sptech.megusta.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.megusta.dto.tipo_status.TipoStatusRequest;
import school.sptech.megusta.dto.tipo_status.TipoStatusResponse;
import school.sptech.megusta.mapper.TipoStatusMapper;
import school.sptech.megusta.model.TipoStatus;
import school.sptech.megusta.service.TipoStatusService;

import java.util.List;

@RestController
@RequestMapping("/tipo-status")
public class TipoStatusController {

    public final TipoStatusService tipoStatusService;

    public TipoStatusController(TipoStatusService tipoStatusService) {
        this.tipoStatusService = tipoStatusService;
    }


    @GetMapping
    public ResponseEntity<List<TipoStatusResponse>> listar(){
        List<TipoStatus> status = tipoStatusService.listar();
        if(status.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(TipoStatusMapper.toResponseList(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoStatusResponse> buscarPorId(@PathVariable Integer id){
        return ResponseEntity.ok(TipoStatusMapper.toResponse(tipoStatusService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<TipoStatusResponse> cadastrar(
            @RequestBody TipoStatusRequest request
    ){
        TipoStatus statusParaCadastrar = TipoStatusMapper.toEntity(request);
        TipoStatus statusSalvo = tipoStatusService.cadastrar(statusParaCadastrar);
        return ResponseEntity.status(201).body(TipoStatusMapper.toResponse(statusSalvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoStatusResponse> atualizar(
            @PathVariable Integer id,
            @RequestBody TipoStatusRequest request
    ){
        TipoStatus status = TipoStatusMapper.toEntity(request);
        TipoStatus salvo = tipoStatusService.atualizar(id, status);
        return ResponseEntity.ok(TipoStatusMapper.toResponse(salvo));
    }

    @DeleteMapping
    public ResponseEntity<Void> excluir(Integer id){
        tipoStatusService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
