package school.sptech.megusta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "04. Fornecedores", description = "Gerenciamento dos fornecedores de insumos e materiais")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @Operation(summary = "Listar todos os fornecedores", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FornecedorResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhum fornecedor cadastrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<FornecedorResponse>> listar(){
        List<Fornecedor> fornecedores = fornecedorService.listar();
        if(fornecedores.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(FornecedorMapper.toResponseList(fornecedores));
    }

    @Operation(summary = "Cadastrar novo fornecedor", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fornecedor cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FornecedorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PostMapping
    public  ResponseEntity<FornecedorResponse> cadastrar(@RequestBody FornecedorRequest request){
        Fornecedor paraCadastrar = FornecedorMapper.toEntity(request);
        Fornecedor cadastrado = fornecedorService.cadastrar(paraCadastrar);
        return ResponseEntity.status(201).body(FornecedorMapper.toResponse(cadastrado));
    }

    @Operation(summary = "Buscar fornecedor por ID", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornecedor encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FornecedorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponse> buscarPorId(@PathVariable Integer id){
        Fornecedor fornecedor = fornecedorService.buscarPorId(id);
        return ResponseEntity.ok(FornecedorMapper.toResponse(fornecedor));
    }

    @Operation(summary = "Atualizar dados do fornecedor", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornecedor atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FornecedorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponse> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid FornecedorRequest request
    ){
        Fornecedor fornecedor = FornecedorMapper.toEntity(request);
        Fornecedor atualizado = fornecedorService.atualizar(id, fornecedor);
        return ResponseEntity.ok(FornecedorMapper.toResponse(atualizado));
    }

    @Operation(summary = "Excluir fornecedor", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fornecedor excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id){
        fornecedorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
