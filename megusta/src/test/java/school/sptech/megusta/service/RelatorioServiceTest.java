package school.sptech.megusta.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.sptech.megusta.dto.relatorio.RelatorioResumoResponseDto;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RelatorioServiceTest {

    private final RelatorioService relatorioService = new RelatorioService();

    @Test
    @DisplayName("Deve listar os relatórios de desenvolvimento")
    void deveListarRelatorios() {
        List<RelatorioResumoResponseDto> relatorios = relatorioService.listar();

        assertNotNull(relatorios);
        assertEquals(4, relatorios.size());
        assertEquals("Relatório 01", relatorios.get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar um relatório existente por ID")
    void deveBuscarRelatorioExistente() {
        RelatorioResumoResponseDto relatorio = relatorioService.buscarPorId(2);

        assertNotNull(relatorio);
        assertEquals(2, relatorio.getId());
        assertEquals("Relatório 02", relatorio.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o relatório não existe")
    void deveLancarExcecaoQuandoRelatorioNaoExiste() {
        Assertions.assertThrows(
                RecursoNaoEncontradoException.class,
                () -> relatorioService.buscarPorId(999)
        );
    }

    @Test
    @DisplayName("Deve gerar um PDF válido para relatório existente")
    void deveGerarPdfValido() {
        byte[] pdf = relatorioService.gerarPdf(1);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
        assertTrue(new String(pdf, 0, Math.min(pdf.length, 5)).contains("%PDF"));
    }
}
