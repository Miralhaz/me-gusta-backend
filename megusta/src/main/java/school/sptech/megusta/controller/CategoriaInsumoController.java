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
import school.sptech.megusta.dto.categoria_insumo.CategoriaInsumoRequestDto;
import school.sptech.megusta.dto.categoria_insumo.CategoriaInsumoResponseDto;
import school.sptech.megusta.dto.consumo_categoria.ConsumoCategoriaRequestDto;
import school.sptech.megusta.dto.consumo_categoria.ConsumoCategoriaResponseDto;
import school.sptech.megusta.dto.insumo.InsumoResponse;
import school.sptech.megusta.mapper.CategoriaInsumoMapper;
import school.sptech.megusta.mapper.InsumoMapper;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.service.CategoriaInsumoService;

import java.util.List;

@RestController
@RequestMapping("/categoria-insumos")
@Tag(name = "07. Categorias de Insumo", description = "Gerenciamento das categorias de ingredientes (ex: Laticínios, Carnes)")
public class CategoriaInsumoController {

    private final CategoriaInsumoService service;

    public CategoriaInsumoController(CategoriaInsumoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todas as categorias de insumo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaInsumoResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma categoria cadastrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<CategoriaInsumoResponseDto>> listar(){
        List<CategoriaInsumo> categoriaInsumos = service.listar();
        if (categoriaInsumos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CategoriaInsumoMapper.toResponseDtoList(categoriaInsumos));
    }

    @Operation(summary = "Buscar categoria de insumo por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaInsumoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaInsumoResponseDto> buscarPorId(@PathVariable Integer id){
        CategoriaInsumo categoriaInsumoAchada = service.buscarPorId(id);
        return ResponseEntity.ok(CategoriaInsumoMapper.toResponseDto(categoriaInsumoAchada));
    }

    @Operation(summary = "Cadastrar nova categoria de insumo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria cadastrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaInsumoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Categoria já cadastrada", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CategoriaInsumoResponseDto> cadastrar(
            @RequestBody @Valid CategoriaInsumoRequestDto request
            ){
        CategoriaInsumo categoriaInsumo = CategoriaInsumoMapper.toEntity(request);
        CategoriaInsumo categoriaCadastrada = service.cadastrar(categoriaInsumo);
        return ResponseEntity.status(201).body(CategoriaInsumoMapper.toResponseDto(categoriaCadastrada));
    }

    @Operation(summary = "Atualizar categoria de insumo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaInsumoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Categoria já cadastrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaInsumoResponseDto> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid CategoriaInsumoRequestDto request
    ){
        CategoriaInsumo categoriaAtualizada = CategoriaInsumoMapper.toEntity(request);
        CategoriaInsumo categoriaSalva = service.atualizar(id, categoriaAtualizada);
        return ResponseEntity.ok(CategoriaInsumoMapper.toResponseDto(categoriaSalva));
    }

    @Operation(summary = "Excluir categoria de insumo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/insumos")
    @Operation(summary = "Listar insumos de uma categoria específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insumos retornados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InsumoResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhum insumo nessa categoria", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    public ResponseEntity<List<InsumoResponse>> listarInsumoDaCategoria(@PathVariable Integer id){
        List<Insumo> insumos = service.listarInsumosPorCategoria(id);
        if (insumos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(InsumoMapper.toResponse(insumos));
    }

    @PostMapping("/consumo")
    @Operation(summary = "Calcular o consumo total dos insumos de uma categoria nos últimos x dias")
    public ResponseEntity<List<ConsumoCategoriaResponseDto>> calcularConsumoPorCategoria(
            @RequestBody @Valid ConsumoCategoriaRequestDto request
    ){
        return ResponseEntity.ok(service.calcularConsumoPorCategoriaNosUltimosDias(request));
    }
}
