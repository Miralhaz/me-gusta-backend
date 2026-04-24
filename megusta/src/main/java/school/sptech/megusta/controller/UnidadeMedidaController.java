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
import school.sptech.megusta.dto.unidade_medida.UnidadeMedidaRequest;
import school.sptech.megusta.dto.unidade_medida.UnidadeMedidaResponse;
import school.sptech.megusta.mapper.UnidadeMedidaMapper;
import school.sptech.megusta.model.UnidadeMedida;
import school.sptech.megusta.service.UnidadeMedidaService;

import java.util.List;

@RestController
@RequestMapping("/unidade-medidas")
@Tag(name = "09. Unidades de Medida", description = "Gerenciamento das unidades de medida (ex: kg, g, L, ml)")
public class UnidadeMedidaController {

    private final UnidadeMedidaService unidadeMedidaService;

    public UnidadeMedidaController(UnidadeMedidaService unidadeMedidaService) {
        this.unidadeMedidaService = unidadeMedidaService;
    }

    @Operation(summary = "Listar todas as unidades de medida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnidadeMedidaResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma unidade cadastrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<UnidadeMedidaResponse>> listar(){
        List<UnidadeMedida> unidadeMedidas = unidadeMedidaService.listar();
        if (unidadeMedidas.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(UnidadeMedidaMapper.toResponse(unidadeMedidas));
    }

    @Operation(summary = "Buscar unidade de medida por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidade encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnidadeMedidaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeMedidaResponse> buscarPorId(@PathVariable Integer id){
        UnidadeMedida unidadeMedida = unidadeMedidaService.buscarPorId(id);
        return ResponseEntity.ok(UnidadeMedidaMapper.toResponse(unidadeMedida));
    }

    @Operation(summary = "Cadastrar nova unidade de medida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Unidade cadastrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnidadeMedidaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Unidade já cadastrada", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UnidadeMedidaResponse> cadastrar(@RequestBody @Valid UnidadeMedidaRequest request){
        UnidadeMedida unidadeMedida = UnidadeMedidaMapper.toEntity(request);
        UnidadeMedida salvo = unidadeMedidaService.cadastrar(unidadeMedida);
        return ResponseEntity.status(201).body(UnidadeMedidaMapper.toResponse(salvo));
    }

    @Operation(summary = "Atualizar unidade de medida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidade atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnidadeMedidaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UnidadeMedidaResponse> atualizar(
            @RequestBody @Valid UnidadeMedidaRequest request,
            @PathVariable Integer id
    ){
        UnidadeMedida unidadeMedida = UnidadeMedidaMapper.toEntity(request);
        UnidadeMedida atualizada = unidadeMedidaService.atualizar(unidadeMedida, id);
        return ResponseEntity.ok(UnidadeMedidaMapper.toResponse(atualizada));
    }

    @Operation(summary = "Excluir unidade de medida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Unidade excluída com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id){
        unidadeMedidaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
