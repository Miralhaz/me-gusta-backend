package school.sptech.megusta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "05. Insumos", description = "Gerenciamento do estoque de ingredientes e materiais")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @Operation(summary = "Listar todos os insumos", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InsumoResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhum insumo cadastrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<InsumoResponse>> listar(){
        List<Insumo> insumos = insumoService.listar();
        return ResponseEntity.ok(InsumoMapper.toResponse(insumos));
    }

    @Operation(summary = "Buscar insumo por ID", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insumo encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InsumoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Insumo não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<InsumoResponse> buscarPorId(@PathVariable Integer id){
        Insumo insumo = insumoService.buscarPorId(id);
        return ResponseEntity.ok(InsumoMapper.toResponse(insumo));
    }

    @Operation(summary = "Cadastrar novo insumo", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Insumo cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InsumoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<InsumoResponse> cadastrar(@RequestBody @Valid InsumoRequest request){
        Insumo insumo = InsumoMapper.toEntity(request);
        Insumo insumoCriado = insumoService.cadastrar(insumo);
        return ResponseEntity.status(201).body(InsumoMapper.toResponse(insumoCriado));
    }

    @Operation(summary = "Atualizar dados do insumo", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insumo atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InsumoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Insumo não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
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
