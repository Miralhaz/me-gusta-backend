package school.sptech.megusta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.sptech.megusta.dto.insumo.InsumoResponse;
import school.sptech.megusta.dto.planilha_vendas.Vendas;
import school.sptech.megusta.service.VendasService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
@Tag(name = "12. Vendas", description = "Gerenciamento de vendas")
public class VendasController {

    private final VendasService vendasService;

    @Operation(summary = "Exibir a planilha de vendas em formato JSON")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Planilha exibida com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Vendas.class))),
            @ApiResponse(responseCode = "400", description = "Formato de arquivo inválido (deve ser xlsx)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PostMapping(
            value = "/importar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<List<Vendas>> exibir(
            @RequestPart("planilha") MultipartFile planilha
            ) throws IOException {

        if (planilha.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String nome = planilha.getOriginalFilename();

        if (nome == null || !nome.endsWith(".xlsx")) {

            return ResponseEntity.badRequest().build();
        }

        List<Vendas> vendas = vendasService.lerPlanilha(planilha);

        return ResponseEntity.ok(vendas);
    }
}
