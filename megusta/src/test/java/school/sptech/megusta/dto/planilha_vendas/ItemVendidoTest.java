package school.sptech.megusta.dto.planilha_vendas;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

@DisplayName("Testes de ItemVendido (agregação por nome)")
class ItemVendidoTest {

    @Test
    @DisplayName("Deve somar as quantidades de itens com o mesmo nome")
    void deveSomarQuantidadesDeItensComMesmoNome() {
        List<ItemVendido> itens = List.of(
                new ItemVendido("Fogazza de Mussarela", BigDecimal.valueOf(2)),
                new ItemVendido("Fogazza de Portuguesa", BigDecimal.valueOf(3)),
                new ItemVendido("Fogazza de Mussarela", BigDecimal.valueOf(5))
        );

        List<ItemVendido> agregados = ItemVendido.agregarPorNome(itens);

        Assertions.assertEquals(2, agregados.size());
        Assertions.assertEquals(BigDecimal.valueOf(7), quantidadeDe(agregados, "Fogazza de Mussarela"));
        Assertions.assertEquals(BigDecimal.valueOf(3), quantidadeDe(agregados, "Fogazza de Portuguesa"));
    }

    @Test
    @DisplayName("Deve ignorar itens sem nome durante a agregação")
    void deveIgnorarItensSemNomeDuranteAgregacao() {
        List<ItemVendido> itens = List.of(
                new ItemVendido("Fogazza de Mussarela", BigDecimal.valueOf(2)),
                new ItemVendido(null, BigDecimal.valueOf(3)),
                new ItemVendido("   ", BigDecimal.valueOf(4))
        );

        List<ItemVendido> agregados = ItemVendido.agregarPorNome(itens);

        Assertions.assertEquals(1, agregados.size());
        Assertions.assertEquals(BigDecimal.valueOf(2), agregados.get(0).getQuantidade());
    }

    @Test
    @DisplayName("Deve tratar quantidade nula como zero na agregação")
    void deveTratarQuantidadeNulaComoZeroNaAgregacao() {
        List<ItemVendido> itens = List.of(
                new ItemVendido("Fogazza de Mussarela", null),
                new ItemVendido("Fogazza de Mussarela", BigDecimal.valueOf(4))
        );

        List<ItemVendido> agregados = ItemVendido.agregarPorNome(itens);

        Assertions.assertEquals(1, agregados.size());
        Assertions.assertEquals(BigDecimal.valueOf(4), agregados.get(0).getQuantidade());
    }

    private BigDecimal quantidadeDe(List<ItemVendido> itens, String nome) {
        return itens.stream()
                .filter(item -> item.getNomeItem().equals(nome))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Item não encontrado: " + nome))
                .getQuantidade();
    }
}