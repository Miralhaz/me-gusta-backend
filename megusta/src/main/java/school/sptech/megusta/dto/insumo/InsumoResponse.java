package school.sptech.megusta.dto.insumo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class InsumoResponse {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Queijo Mussarela")
    private String nome;

    @Schema(example = "QM-001")
    private String codigoInsumo;

    @Schema(example = "5.0")
    private Double estoqueMinimo;

    @Schema(example = "20.0")
    private Double quantidadeAtual;

    @Schema(example = "true")
    private boolean ativo;

    private InsumoCategoria insumoCategoria;
    private UnidadeInsumo unidadeInsumo;
    private TipoStatusInsumo tipoStatus;

    public static class TipoStatusInsumo {
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "OK")
        private String nome;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

    public static class InsumoCategoria {
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "Laticínios")
        private String nome;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

    public static class UnidadeInsumo {
        @Schema(example = "1")
        private Integer id;

        @Schema(example = "kg")
        private String unidade;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUnidade() {
            return unidade;
        }

        public void setUnidade(String unidade) {
            this.unidade = unidade;
        }
    }

    public TipoStatusInsumo getTipoStatus() {
        return tipoStatus;
    }

    public void setTipoStatus(TipoStatusInsumo tipoStatus) {
        this.tipoStatus = tipoStatus;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getCodigoInsumo() {
        return codigoInsumo;
    }

    public void setCodigoInsumo(String codigoInsumo) {
        this.codigoInsumo = codigoInsumo;
    }

    public Double getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Double estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public InsumoCategoria getInsumoCategoria() {
        return insumoCategoria;
    }

    public void setInsumoCategoria(InsumoCategoria insumoCategoria) {
        this.insumoCategoria = insumoCategoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(Double quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public UnidadeInsumo getUnidadeInsumo() {
        return unidadeInsumo;
    }

    public void setUnidadeInsumo(UnidadeInsumo unidadeInsumo) {
        this.unidadeInsumo = unidadeInsumo;
    }
}
