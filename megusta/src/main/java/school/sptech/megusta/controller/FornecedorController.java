package school.sptech.megusta.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.megusta.dto.fornecedor.FornecedorRequest;
import school.sptech.megusta.dto.fornecedor.FornecedorResponse;
import school.sptech.megusta.mapper.FornecedorMapper;
import school.sptech.megusta.model.Fornecedor;
import school.sptech.megusta.service.FornecedorService;

import java.util.List;

@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }


    @GetMapping
    public ResponseEntity<List<FornecedorResponse>> listar(){
        List<Fornecedor> fornecedores = fornecedorService.listar();
        if(fornecedores.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(FornecedorMapper.toResponseList(fornecedores));
    }

    @PostMapping
    public  ResponseEntity<FornecedorResponse> cadastrar(@RequestBody FornecedorRequest request){
        Fornecedor paraCadastrar = FornecedorMapper.toEntity(request);
        Fornecedor cadastrado = fornecedorService.cadastrar(paraCadastrar);
        return ResponseEntity.status(201).body(FornecedorMapper.toResponse(cadastrado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponse> buscarPorId(@PathVariable Integer id){
        Fornecedor fornecedor = fornecedorService.buscarPorId(id);
        return ResponseEntity.ok(FornecedorMapper.toResponse(fornecedor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponse> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid FornecedorRequest request
    ){
        Fornecedor fornecedor = FornecedorMapper.toEntity(request);
        Fornecedor atualizado = fornecedorService.atualizar(id, fornecedor);
        return ResponseEntity.ok(FornecedorMapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id){
        fornecedorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
