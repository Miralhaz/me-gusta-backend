package school.sptech.megusta.dto.relatorio;

import java.time.LocalDate;
import java.util.List;

public class RelatorioRequestDTO {

    private String tipo;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private List<TipoItemRelatorio> itens;

    private Integer insumoId;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public List<TipoItemRelatorio> getItens() {
        return itens;
    }

    public void setItens(List<TipoItemRelatorio> itens) {
        this.itens = itens;
    }

    public Integer getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(Integer insumoId) {
        this.insumoId = insumoId;
    }
}