package school.sptech.megusta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import school.sptech.megusta.dto.planilha_vendas.BaixaInsumoResponse;
import school.sptech.megusta.service.VendasService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
@Tag(name = "12. Vendas", description = "Gerenciamento de vendas")
public class VendasController {

    private final VendasService vendasService;

    @Operation(summary = "Importar relatório de itens vendidos: aplica a baixa de estoque e devolve os insumos alterados",
            description = "Aceita somente o relatório de itens vendidos da plataforma, reconhecido pela presença das "
                    + "colunas 'Nome Prod' e 'Qtd.' na linha de cabeçalho. O casamento dos cabeçalhos é insensível a "
                    + "maiúsculas/minúsculas e o índice das colunas é indiferente: elas podem estar em qualquer posição "
                    + "e em qualquer ordem. Todas as demais colunas do arquivo (datas, valores, códigos de pedido, "
                    + "taxas etc.) são ignoradas. A baixa de estoque é transacional e a resposta lista um registro por "
                    + "insumo alterado, com a quantidade antes da subtração, a quantidade subtraída e a restante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório importado com sucesso; a lista traz um registro por insumo alterado (lista vazia quando nenhuma quantidade é alterada, por exemplo quando nenhum nome corresponde a uma fogazza cadastrada)",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaixaInsumoResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Arquivo vazio, extensão diferente de .xlsx ou layout não suportado (sem as colunas 'Nome Prod' e 'Qtd.'); nenhuma alteração é persistida", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Estoque insuficiente para baixar os insumos importados; nenhuma alteração é persistida", content = @Content)
    })
    @PostMapping(
            value = "/importar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<List<BaixaInsumoResponse>> importar(
            @RequestPart("planilha") MultipartFile planilha
            ) throws IOException {

        if (planilha.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String nome = planilha.getOriginalFilename();

        if (nome == null || !nome.endsWith(".xlsx")) {

            return ResponseEntity.badRequest().build();
        }

        List<BaixaInsumoResponse> baixas = vendasService.importarPlanilha(planilha);

        return ResponseEntity.ok(baixas);
    }
}