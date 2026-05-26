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
import school.sptech.megusta.dto.entrada_estoque.EntradaEstoqueRequest;
import school.sptech.megusta.dto.entrada_estoque.EntradaEstoqueResponse;
import school.sptech.megusta.mapper.EntradaEstoqueMapper;
import school.sptech.megusta.model.EntradaEstoque;
import school.sptech.megusta.service.EntradaEstoqueService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/entradas-estoque")
@Tag(name = "06. Entradas de Estoque", description = "Gerenciamento de entradas de produtos no estoque")
public class EntradaEstoqueController {

    private final EntradaEstoqueService entradaEstoqueService;

    public EntradaEstoqueController(EntradaEstoqueService entradaEstoqueService) {
        this.entradaEstoqueService = entradaEstoqueService;
    }

    @Operation(summary = "Listar todas as entradas de estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntradaEstoqueResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma entrada de estoque cadastrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<EntradaEstoqueResponse>> listar() {
        List<EntradaEstoque> entradas = entradaEstoqueService.listar();
        if (entradas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(EntradaEstoqueMapper.toResponse(entradas));
    }

    @Operation(summary = "Buscar entrada de estoque por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrada encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntradaEstoqueResponse.class))),
            @ApiResponse(responseCode = "404", description = "Entrada não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntradaEstoqueResponse> buscarPorId(@PathVariable Integer id) {
        EntradaEstoque entrada = entradaEstoqueService.buscarPorId(id);
        return ResponseEntity.ok(EntradaEstoqueMapper.toResponse(entrada));
    }

    @Operation(summary = "Cadastrar nova entrada de estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrada cadastrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntradaEstoqueResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso referenciado não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntradaEstoqueResponse> cadastrar(@RequestBody @Valid EntradaEstoqueRequest request) {
        EntradaEstoque entrada = EntradaEstoqueMapper.toEntity(request);
        EntradaEstoque entradaCriada = entradaEstoqueService.cadastrar(entrada);
        return ResponseEntity.status(201).body(EntradaEstoqueMapper.toResponse(entradaCriada));
    }

    @Operation(summary = "Atualizar dados da entrada de estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrada atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntradaEstoqueResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Entrada não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntradaEstoqueResponse> atualizar(
            @RequestBody @Valid EntradaEstoqueRequest request,
            @PathVariable Integer id
    ) {
        EntradaEstoque entrada = EntradaEstoqueMapper.toEntity(request);
        EntradaEstoque entradaAtualizada = entradaEstoqueService.atualizar(entrada, id);
        return ResponseEntity.ok(EntradaEstoqueMapper.toResponse(entradaAtualizada));
    }

    @Operation(summary = "Deletar entrada de estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Entrada deletada com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Entrada não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        entradaEstoqueService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar entradas por insumo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entradas encontradas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntradaEstoqueResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma entrada encontrada", content = @Content),
            @ApiResponse(responseCode = "404", description = "Insumo não encontrado", content = @Content)
    })
    @GetMapping("/insumo/{idInsumo}")
    public ResponseEntity<List<EntradaEstoqueResponse>> buscarPorInsumo(@PathVariable Integer idInsumo) {
        List<EntradaEstoque> entradas = entradaEstoqueService.buscarPorInsumo(idInsumo);
        if (entradas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(EntradaEstoqueMapper.toResponse(entradas));
    }

    @Operation(summary = "Buscar entradas por fornecedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entradas encontradas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntradaEstoqueResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma entrada encontrada", content = @Content),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado", content = @Content)
    })
    @GetMapping("/fornecedor/{idFornecedor}")
    public ResponseEntity<List<EntradaEstoqueResponse>> buscarPorFornecedor(@PathVariable Integer idFornecedor) {
        List<EntradaEstoque> entradas = entradaEstoqueService.buscarPorFornecedor(idFornecedor);
        if (entradas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(EntradaEstoqueMapper.toResponse(entradas));
    }

    @Operation(summary = "Buscar entradas por período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entradas encontradas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntradaEstoqueResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma entrada encontrada", content = @Content)
    })
    @GetMapping("/periodo")
    public ResponseEntity<List<EntradaEstoqueResponse>> buscarPorPeriodo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim
    ) {
        List<EntradaEstoque> entradas = entradaEstoqueService.buscarPorDataPedido(dataInicio, dataFim);
        if (entradas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(EntradaEstoqueMapper.toResponse(entradas));
    }

}