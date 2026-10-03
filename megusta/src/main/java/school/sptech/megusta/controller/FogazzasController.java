package school.sptech.megusta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.megusta.dto.Fogazzas.FogazzasRequestDto;
import school.sptech.megusta.dto.Fogazzas.FogazzasResponseDto;
import school.sptech.megusta.mapper.FogazzasMapper;
import school.sptech.megusta.model.Fogazzas;
import school.sptech.megusta.service.FogazzasService;

import java.util.List;

@RestController
@RequestMapping("/fogazzas")
@Tag(name = "03. Cardápio de Fogazzas", description = "Gerenciamento dos sabores e categorias de fogazzas artesanais")
public class FogazzasController {
    private final FogazzasService service;

    public FogazzasController(FogazzasService service) {
        this.service = service;
    }

    @Operation(summary = "Listar fogazzas com paginação",
            description = "Parâmetros opcionais: page (começa em 0), size (padrão 10), sort (campo,direção)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página retornada com sucesso",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/paginado")
    public ResponseEntity<PagedModel<FogazzasResponseDto>> listarFogazzaPaginado(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<Fogazzas> pagina = service.listarPaginado(pageable);
        Page<FogazzasResponseDto> resposta = pagina.map(FogazzasMapper::toResponseDto);
        return ResponseEntity.ok(new PagedModel<>(resposta));
    }

    @Operation(summary = "Buscar fogazza por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fogazza encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FogazzasResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Fogazza não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })

    @GetMapping
    public ResponseEntity<List<FogazzasResponseDto>> listarTodos() {
        List<Fogazzas> fogazzas = service.listar();
        if (fogazzas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(FogazzasMapper.toResponseDtoList(fogazzas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FogazzasResponseDto> buscarPorId(@PathVariable Integer id) {
        Fogazzas fogazza = service.buscarPorId(id);
        return ResponseEntity.ok(FogazzasMapper.toResponseDto(fogazza));
    }

    @Operation(summary = "Cadastrar novo sabor de fogazza")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fogazza cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Sabor já cadastrado")
    })
    @PostMapping
    public ResponseEntity<FogazzasResponseDto> cadastrar(@RequestBody @Valid FogazzasRequestDto request) {
        Fogazzas fogazza = FogazzasMapper.toEntity(request);
        Fogazzas fogazzaCadastrada = service.cadastrar(fogazza, request.getCategoriaFogazzaId());
        return ResponseEntity.status(201).body(FogazzasMapper.toResponseDto(fogazzaCadastrada));
    }

    @Operation(summary = "Atualizar dados da fogazza")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fogazza atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FogazzasResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Fogazza não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<FogazzasResponseDto> atualizar(@PathVariable Integer id,
                                                        @RequestBody @Valid
                                                        FogazzasRequestDto request) {
        Fogazzas fogazza = FogazzasMapper.toEntity(request);
        Fogazzas fogazzaAtualizada = service.atualizar(
                id, fogazza, request.getCategoriaFogazzaId());
        return ResponseEntity.ok(FogazzasMapper.toResponseDto(fogazzaAtualizada));
    }

    @Operation(summary = "Excluir sabor de fogazza")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fogazza excluída com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Fogazza não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
