package school.sptech.megusta.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.megusta.dto.saida_estoque.SaidaEstoqueRequest;
import school.sptech.megusta.dto.saida_estoque.SaidaEstoqueResponse;
import school.sptech.megusta.mapper.SaidaEstoqueMapper;
import school.sptech.megusta.model.SaidaEstoque;
import school.sptech.megusta.service.SaidaEstoqueService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/saidas-estoque")
public class SaidaEstoqueController {

    private final SaidaEstoqueService service;

    public SaidaEstoqueController(SaidaEstoqueService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SaidaEstoqueResponse>> listar() {
        List<SaidaEstoque> list = service.listar();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(SaidaEstoqueMapper.toResponse(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaidaEstoqueResponse> buscarPorId(@PathVariable Integer id) {
        SaidaEstoque s = service.buscarPorId(id);
        return ResponseEntity.ok(SaidaEstoqueMapper.toResponse(s));
    }

    @PostMapping
    public ResponseEntity<SaidaEstoqueResponse> cadastrar(@Valid @RequestBody SaidaEstoqueRequest request) {
        SaidaEstoque entity = SaidaEstoqueMapper.toEntity(request);
        SaidaEstoque criado = service.cadastrar(entity);
        return ResponseEntity.status(201).body(SaidaEstoqueMapper.toResponse(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaidaEstoqueResponse> atualizar(@Valid @RequestBody SaidaEstoqueRequest request,
                                                          @PathVariable Integer id) {
        SaidaEstoque entity = SaidaEstoqueMapper.toEntity(request);
        SaidaEstoque atualizado = service.atualizar(entity, id);
        return ResponseEntity.ok(SaidaEstoqueMapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/insumo/{idInsumo}")
    public ResponseEntity<List<SaidaEstoqueResponse>> buscarPorInsumo(@PathVariable Integer idInsumo) {
        List<SaidaEstoque> list = service.buscarPorInsumo(idInsumo);
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(SaidaEstoqueMapper.toResponse(list));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<SaidaEstoqueResponse>> buscarPorPeriodo(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fim) {
        List<SaidaEstoque> list = service.buscarPorPeriodo(inicio, fim);
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(SaidaEstoqueMapper.toResponse(list));
    }
}