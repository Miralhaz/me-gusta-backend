package school.sptech.megusta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.megusta.dto.relatorio.RelatorioResumoResponseDto;
import school.sptech.megusta.service.RelatorioService;

import java.util.List;

@RestController
@RequestMapping("/relatorios")
@Tag(name = "15. Relatórios", description = "Listagem e download de relatórios do sistema")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @Operation(summary = "Listar todos os relatórios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RelatorioResumoResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Nenhum relatório cadastrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<RelatorioResumoResponseDto>> listar() {
        List<RelatorioResumoResponseDto> relatorios = relatorioService.listar();
        if (relatorios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(relatorios);
    }

    @Operation(summary = "Buscar relatório por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RelatorioResumoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RelatorioResumoResponseDto> buscarPorId(@PathVariable Integer id) {
        RelatorioResumoResponseDto relatorio = relatorioService.buscarPorId(id);
        return ResponseEntity.ok(relatorio);
    }

    @Operation(summary = "Baixar relatório em PDF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado com sucesso",
                    content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> baixarPdf(@PathVariable Integer id) {
        byte[] pdf = relatorioService.gerarPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("relatorio-" + id + ".pdf")
                .build());

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
