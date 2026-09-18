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
import school.sptech.megusta.dto.planilha_vendas.ItemVendido;
import school.sptech.megusta.service.VendasService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
@Tag(name = "12. Vendas", description = "Gerenciamento de vendas")
public class VendasController {

    private final VendasService vendasService;

    @Operation(summary = "Importar planilha de vendas: extrai os itens em JSON e aplica a baixa de estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Planilha processada com sucesso; baixa de estoque aplicada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemVendido.class))),
            @ApiResponse(responseCode = "400", description = "Arquivo vazio ou formato de arquivo inválido (deve ser .xlsx); nenhuma alteração é persistida", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Estoque insuficiente para baixar os itens importados; nenhuma alteração é persistida", content = @Content)
    })
    @PostMapping(
            value = "/importar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<List<ItemVendido>> importar(
            @RequestPart("planilha") MultipartFile planilha
            ) throws IOException {

        if (planilha.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String nome = planilha.getOriginalFilename();

        if (nome == null || !nome.endsWith(".xlsx")) {

            return ResponseEntity.badRequest().build();
        }

        List<ItemVendido> itens = vendasService.importarPlanilha(planilha);

        return ResponseEntity.ok(itens);
    }
}