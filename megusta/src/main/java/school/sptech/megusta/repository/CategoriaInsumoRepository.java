package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.sptech.megusta.dto.consumo_categoria.ConsumoCategoriaResponseDto;
import school.sptech.megusta.dto.consumo_intermediario_categoria.ConsumoIntermediarioCategoriaResponseDto;
import school.sptech.megusta.model.CategoriaInsumo;

import java.time.LocalDateTime;
import java.util.List;

public interface CategoriaInsumoRepository extends JpaRepository<CategoriaInsumo, Integer> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Integer id);

    @Query("""
    SELECT new school.sptech.megusta.dto.consumo_categoria.ConsumoCategoriaResponseDto(SUM(se.quantidade), CAST(se.dtSaida AS localdate))
    FROM SaidaEstoque se
    JOIN se.insumo i
    JOIN i.categoriaInsumo ci
    WHERE ci.nome = :nomeCategoria AND se.dtSaida >= :dataAnterior
    GROUP BY CAST(se.dtSaida AS localdate)
    ORDER BY CAST(se.dtSaida AS localdate)
""")
    List<ConsumoCategoriaResponseDto> consumoPorCategoriaEspecifica(String nomeCategoria, LocalDateTime dataAnterior);

    @Query("""
    SELECT new school.sptech.megusta.dto.consumo_intermediario_categoria.ConsumoIntermediarioCategoriaResponseDto(SUM(se.quantidade), CAST(se.dtSaida AS localdate), ci.nome)
    FROM SaidaEstoque se
    JOIN se.insumo i
    JOIN i.categoriaInsumo ci
    WHERE se.dtSaida >= :dataAnterior
    GROUP BY ci.nome, CAST(se.dtSaida AS localdate)
    ORDER BY ci.nome, CAST(se.dtSaida AS localdate)
    """)
    List<ConsumoIntermediarioCategoriaResponseDto> consumoPorTodasAsCategorias(LocalDateTime dataAnterior);
}