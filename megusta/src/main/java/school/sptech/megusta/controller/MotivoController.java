package school.sptech.megusta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.megusta.dto.insumo.InsumoResponse;
import school.sptech.megusta.dto.motivo.MotivoRequest;
import school.sptech.megusta.dto.motivo.MotivoResponse;
import school.sptech.megusta.mapper.MotivoMapper;
import school.sptech.megusta.service.MotivoService;

import java.util.List;

@RestController
@RequestMapping("/motivos")
@Tag(name = "10. Motivos", description = "Motivos pelas saídas de insumos do estoque")
@RequiredArgsConstructor
public class MotivoController {

    private final MotivoService motivoService;

    @Operation(summary = "Listar todos os motivos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MotivoResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhum motivo cadastrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<MotivoResponse>> buscarTodos(){
        List<MotivoResponse> responseList = motivoService.buscarTodos();

        if (responseList.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "Buscar motivo por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Motivo encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MotivoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Motivo não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<MotivoResponse> buscarPorId(@PathVariable Integer id){
        return ResponseEntity.ok(motivoService.buscarPorId(id));
    }

    @Operation(summary = "Cadastrar novo motivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Motivo cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MotivoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe motivo com o mesmo nome", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<MotivoResponse> registrar(@RequestBody MotivoRequest request){
        return ResponseEntity.status(201).body(motivoService.registrar(request));
    }

    @Operation(summary = "Atualizar dados do motivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Motivo atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MotivoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Motivo não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe motivo com o mesmo nome", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<MotivoResponse> atualizar(@PathVariable Integer id,
                                                    @RequestBody MotivoRequest request){
        return ResponseEntity.status(200).body(motivoService.atualizar(id, request));
    }

    @Operation(summary = "Deletar o motivo pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Motivo deletado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MotivoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Motivo não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPorId(@PathVariable Integer id){
        motivoService.deletarPorId(id);
        return ResponseEntity.ok().build();
    }
}
