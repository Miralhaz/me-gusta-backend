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
import school.sptech.megusta.dto.categoria_fogazza.CategoriaFogazzaRequestDto;
import school.sptech.megusta.dto.categoria_fogazza.CategoriaFogazzaResponseDto;
import school.sptech.megusta.dto.categoria_insumo.CategoriaInsumoRequestDto;
import school.sptech.megusta.dto.categoria_insumo.CategoriaInsumoResponseDto;
import school.sptech.megusta.mapper.CategoriaFogazzaMapper;
import school.sptech.megusta.mapper.CategoriaInsumoMapper;
import school.sptech.megusta.model.CategoriaFogazza;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.service.CategoriaFogazzaService;

import java.util.List;

@RestController
@RequestMapping("/categoria-fogazza")
@Tag(name = "06. Categorias de Fogazza", description = "Gerenciamento das categorias (ex: Salgadas, Doces, Especiais)")
public class CategoriaFogazzaController {

    private final CategoriaFogazzaService service;

    public CategoriaFogazzaController(CategoriaFogazzaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todas as categorias de fogazza", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaFogazzaResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma categoria cadastrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<CategoriaFogazzaResponseDto>> listar(){
        List<CategoriaFogazza> categoriaFogazzaList = service.listar();
        if (categoriaFogazzaList.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CategoriaFogazzaMapper.toResponseDtoList(categoriaFogazzaList));
    }

    @Operation(summary = "Buscar categoria por ID", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaFogazzaResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaFogazzaResponseDto> buscarPorId(@PathVariable Integer id){
        CategoriaFogazza categoriaFogazzaCapturada = service.buscarPorId(id);
        return ResponseEntity.ok(CategoriaFogazzaMapper.toResponseDto(categoriaFogazzaCapturada));
    }

    @Operation(summary = "Cadastrar nova categoria de fogazza", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria cadastrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaFogazzaResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Sabor já cadastrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CategoriaFogazzaResponseDto> cadastrar(
            @RequestBody @Valid CategoriaFogazzaRequestDto request
    ){
        CategoriaFogazza categoriaFogazza = CategoriaFogazzaMapper.toEntity(request);
        CategoriaFogazza categoriaCadastrada = service.cadastrar(categoriaFogazza);
        return ResponseEntity.status(201).body(CategoriaFogazzaMapper.toResponseDto
                (categoriaCadastrada));
    }

    @Operation(summary = "Excluir categoria de fogazza", security = @SecurityRequirement(name = "Bearer"))
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
}

