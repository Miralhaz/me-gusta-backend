package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.dto.relatorio.RelatorioResumoResponseDto;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class RelatorioService {

    private static final List<RelatorioResumoResponseDto> RELATORIOS = initRelatorios();

    public List<RelatorioResumoResponseDto> listar() {
        return new ArrayList<>(RELATORIOS);
    }

    public RelatorioResumoResponseDto buscarPorId(Integer id) {
        return RELATORIOS.stream()
                .filter(relatorio -> relatorio.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relatório não encontrado."));
    }

    public byte[] gerarPdf(Integer id) {
        RelatorioResumoResponseDto relatorio = buscarPorId(id);
        String titulo = relatorio.getNome();
        String corpo = "Relatório\n"
                + "Data: " + relatorio.getData() + "\n"
                + "Horário comercial: " + relatorio.getHorarioComercial() + "\n"
                + "Tamanho: " + relatorio.getTamanho() + "\n"
                + "Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        return gerarPdfBytes(titulo, corpo);
    }

    private static List<RelatorioResumoResponseDto> initRelatorios() {
        List<RelatorioResumoResponseDto> relatorios = new ArrayList<>();

        RelatorioResumoResponseDto relatorio1 = new RelatorioResumoResponseDto();
        relatorio1.setId(1);
        relatorio1.setNome("Relatório 01");
        relatorio1.setData("22/03/2026");
        relatorio1.setHorarioComercial("08:00 - 17:00");
        relatorio1.setTamanho("10 Mb");

        RelatorioResumoResponseDto relatorio2 = new RelatorioResumoResponseDto();
        relatorio2.setId(2);
        relatorio2.setNome("Relatório 02");
        relatorio2.setData("23/03/2026");
        relatorio2.setHorarioComercial("08:00 - 17:00");
        relatorio2.setTamanho("6 Mb");

        RelatorioResumoResponseDto relatorio3 = new RelatorioResumoResponseDto();
        relatorio3.setId(3);
        relatorio3.setNome("Relatório 03");
        relatorio3.setData("24/03/2026");
        relatorio3.setHorarioComercial("08:00 - 17:00");
        relatorio3.setTamanho("5 Mb");

        RelatorioResumoResponseDto relatorio4 = new RelatorioResumoResponseDto();
        relatorio4.setId(4);
        relatorio4.setNome("Relatório 04");
        relatorio4.setData("25/03/2026");
        relatorio4.setHorarioComercial("08:00 - 17:00");
        relatorio4.setTamanho("8 Mb");

        relatorios.add(relatorio1);
        relatorios.add(relatorio2);
        relatorios.add(relatorio3);
        relatorios.add(relatorio4);
        return relatorios;
    }

    private byte[] gerarPdfBytes(String titulo, String corpo) {
        String[] linhas = corpo.split("\\n");
        StringBuilder stream = new StringBuilder();
        stream.append("BT\n");
        stream.append("/F1 20 Tf\n");
        stream.append("72 790 Td\n");
        stream.append("(").append(escapePdfText(titulo)).append(") Tj\n");
        stream.append("0 -30 Td\n");
        stream.append("/F1 12 Tf\n");

        for (String linha : linhas) {
            if (linha.equals("Relatório")) {
                continue;
            }
            stream.append("(").append(escapePdfText(linha)).append(") Tj\n");
            stream.append("0 -18 Td\n");
        }

        stream.append("ET");

        String streamText = stream.toString();
        byte[] streamBytes = streamText.getBytes(StandardCharsets.UTF_8);

        List<String> objects = new ArrayList<>();
        objects.add("<< /Type /Catalog /Pages 2 0 R >>");
        objects.add("<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
        objects.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>");
        objects.add("<< /Length " + streamBytes.length + " >>\nstream\n" + streamText + "\nendstream");
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");

        StringBuilder pdf = new StringBuilder();
        pdf.append("%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();

        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.length());
            pdf.append(i).append(" 0 obj\n");
            pdf.append(objects.get(i)).append("\nendobj\n");
        }

        int xrefStart = pdf.length();
        pdf.append("xref\n");
        pdf.append("0 ").append(objects.size() + 1).append("\n");
        pdf.append("0000000000 65535 f \n");
        for (Integer offset : offsets) {
            pdf.append(String.format("%010d 00000 n \n", offset));
        }

        pdf.append("trailer\n");
        pdf.append("<< /Size ").append(objects.size() + 1).append(" /Root 1 0 R >>\n");
        pdf.append("startxref\n");
        pdf.append(xrefStart).append("\n");
        pdf.append("%%EOF");

        return pdf.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private String escapePdfText(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\n", "\\n");
    }
}
