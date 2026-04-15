package school.sptech.megusta.dto.insumo;

import java.time.LocalDateTime;

public class InsumoResponse {

    private Integer id;
    private String nome;
    private String codigoInsumo;
    private Double estoqueMinimo;
    private Double quantidadeAtual;
    private boolean ativo;
    private InsumoCategoria insumoCategoria;
    private UnidadeInsumo unidadeInsumo;

    public static class InsumoCategoria {
        private Integer id;
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
        private Integer id;
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
