package school.sptech.megusta.dto.planilha_vendas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelo interno normalizado de um item vendido extraído de uma planilha de
 * vendas. Todos os três formatos suportados (histórico de itens vendidos,
 * pedidos recentes e relatório de cardápio) são convertidos para este modelo.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemVendido {

    private String nomeItem;

    private BigDecimal quantidade;

    /**
     * Agrega os itens por nome (case-sensitive), somando as quantidades.
     * Itens sem nome são ignorados; quantidade nula é tratada como zero.
     */
    public static List<ItemVendido> agregarPorNome(List<ItemVendido> itens) {
        Map<String, BigDecimal> totais = new LinkedHashMap<>();
        for (ItemVendido item : itens) {
            if (item.getNomeItem() == null || item.getNomeItem().isBlank()) {
                continue;
            }
            BigDecimal quantidade = item.getQuantidade() == null ? BigDecimal.ZERO : item.getQuantidade();
            totais.merge(item.getNomeItem(), quantidade, BigDecimal::add);
        }
        return totais.entrySet().stream()
                .map(entry -> new ItemVendido(entry.getKey(), entry.getValue()))
                .toList();
    }
}