package school.sptech.megusta.dto.relatorio;

import lombok.Getter;
import lombok.Setter;
import school.sptech.megusta.dto.consumo_geral_categoria.ConsumoGeralCategoriaResponseDto;
import school.sptech.megusta.dto.entrada_estoque.EntradaEstoqueResponse;
import school.sptech.megusta.dto.insumo.InsumoResponse;
import school.sptech.megusta.dto.ruptura_insumo.RupturaInsumoResponseDto;
import school.sptech.megusta.dto.saida_estoque.SaidaEstoqueResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RelatorioResponseDto {

    private String titulo;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    private List<ConsumoInsumoRelatorioDto> insumosMaisUtilizados;

    private List<ConsumoInsumoRelatorioDto> consumoPorInsumo;

    private List<ConsumoGeralCategoriaResponseDto> consumoPorCategoria;

    private List<RupturaInsumoResponseDto> mediaConsumo;

    private List<SaidaEstoqueResponse> saidas;

    private List<MotivoSaidaRelatorioDto> motivosSaida;

    private List<MotivoSaidaRelatorioDto> perdas;

    private List<EntradaEstoqueResponse> entradas;

    private BigDecimal valorTotalEntradas;

    private List<FornecedorAbastecimentoRelatorioDto> fornecedores;

    private List<EntradaEstoqueResponse> proximosVencimento;

    private List<EntradaEstoqueResponse> vencidos;

    private List<InsumoResponse> estoqueAtual;

    private List<RupturaInsumoResponseDto> abaixoEstoqueMinimo;

    private EntradaSaidaInsumoRelatorioDto entradaSaidaInsumo;
}