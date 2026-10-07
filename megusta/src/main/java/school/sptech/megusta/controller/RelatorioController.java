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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.sptech.megusta.dto.relatorio.RelatorioRequestDto;
import school.sptech.megusta.dto.relatorio.RelatorioResponseDto;
import school.sptech.megusta.service.RelatorioService;

@RestController
@RequestMapping("/relatorios")
@Tag(name = "15. Relatórios", description = "Geração dinâmica e download de relatórios do sistema")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    // GERAR RELATÓRIO - JSON
    @Operation(summary = "Gerar relatório personalizado",
            description = """ 
                    Gera um relatório com base no período e nos
                    indicadores selecionados pelo usuário.

                    O relatório é produzido com dados atualizados
                    do banco de dados e não é armazenado.
                    
                    O usuário pode selecionar diferentes indicadores,
                    como entradas, saídas, consumo de insumos,
                    estoque atual, vencimentos e fornecedores.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RelatorioResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados ou filtros inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)})

    @PostMapping(value = "/gerar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RelatorioResponseDto> gerarRelatorio(@RequestBody RelatorioRequestDto request) {

        RelatorioResponseDto relatorio =
                relatorioService.gerarRelatorio(request);

        return ResponseEntity.ok(relatorio);
    }

    // GERAR E BAIXAR RELATÓRIO - PDF
    @Operation(summary = "Gerar e baixar relatório em PDF",
            description = """
                    Gera um arquivo PDF com base no período
                    e nos indicadores selecionados pelo usuário.

                    O arquivo é gerado sob demanda,
                    sem armazenamento no banco de dados
                    ou em serviços externos.

                    O download é disponibilizado diretamente
                    na resposta da requisição.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado com sucesso",
                    content = @Content(mediaType = "application/pdf",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "400", description = "Dados ou filtros inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)})

    @PostMapping(value = "/pdf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> baixarPdf(@RequestBody RelatorioRequestDto request) {

        byte[] pdf = relatorioService.gerarPdf(request);

        String nomeArquivo = "relatorio-" + request.getDataInicio() + "-a-" + request.getDataFim() + ".pdf";

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(nomeArquivo).build());
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}