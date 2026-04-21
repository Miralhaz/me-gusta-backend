package school.sptech.megusta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.megusta.dto.tipo_status.TipoStatusRequest;
import school.sptech.megusta.dto.tipo_status.TipoStatusResponse;
import school.sptech.megusta.mapper.TipoStatusMapper;
import school.sptech.megusta.model.TipoStatus;
import school.sptech.megusta.service.TipoStatusService;

import java.util.List;

@RestController
@RequestMapping("/tipo-status")
@Tag(name = "08. Tipos de Status", description = "Gerenciamento dos status de pedidos e processos")
public class TipoStatusController {

    public final TipoStatusService tipoStatusService;

    public TipoStatusController(TipoStatusService tipoStatusService) {
        this.tipoStatusService = tipoStatusService;
    }


    @Operation(summary = "Listar todos os tipos de status", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoStatusResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhum status cadastrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<TipoStatusResponse>> listar(){
        List<TipoStatus> status = tipoStatusService.listar();
        if(status.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(TipoStatusMapper.toResponseList(status));
    }

    @Operation(summary = "Buscar status por ID", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "Status não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TipoStatusResponse> buscarPorId(@PathVariable Integer id){
        return ResponseEntity.ok(TipoStatusMapper.toResponse(tipoStatusService.buscarPorId(id)));
    }

    @Operation(summary = "Cadastrar novo tipo de status", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Status cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Status já cadastrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TipoStatusResponse> cadastrar(
            @RequestBody TipoStatusRequest request
    ){
        TipoStatus statusParaCadastrar = TipoStatusMapper.toEntity(request);
        TipoStatus statusSalvo = tipoStatusService.cadastrar(statusParaCadastrar);
        return ResponseEntity.status(201).body(TipoStatusMapper.toResponse(statusSalvo));
    }

    @Operation(summary = "Atualizar tipo de status", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Status não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TipoStatusResponse> atualizar(
            @PathVariable Integer id,
            @RequestBody TipoStatusRequest request
    ){
        TipoStatus status = TipoStatusMapper.toEntity(request);
        TipoStatus salvo = tipoStatusService.atualizar(id, status);
        return ResponseEntity.ok(TipoStatusMapper.toResponse(salvo));
    }

    @Operation(summary = "Excluir tipo de status", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Status excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Status não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @DeleteMapping
    public ResponseEntity<Void> excluir(Integer id){
        tipoStatusService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
