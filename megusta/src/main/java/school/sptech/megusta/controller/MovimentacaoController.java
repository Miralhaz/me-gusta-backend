package school.sptech.megusta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.megusta.dto.movimentacao.MovimentacaoResponse;
import school.sptech.megusta.service.MovimentacaoService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/movimentacoes")
@Tag(name = "16. Movimentações", description = "Consulta unificada de entradas e saídas de estoque")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @Operation(summary = "Buscar movimentações por período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimentações encontradas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimentacaoResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma movimentação encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/periodo")
    public ResponseEntity<List<MovimentacaoResponse>> buscarPorPeriodo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim
    ) {
        List<MovimentacaoResponse> movimentacoes = movimentacaoService.buscarPorPeriodo(dataInicio, dataFim);
        if (movimentacoes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(movimentacoes);
    }
}